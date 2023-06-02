package it.polito.mad.g26.playingcourtreservation.fragment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import androidx.constraintlayout.widget.Guideline
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.adapter.UserSkillsAdapter
import it.polito.mad.g26.playingcourtreservation.model.User
import it.polito.mad.g26.playingcourtreservation.util.UiState
import it.polito.mad.g26.playingcourtreservation.util.makeGone
import it.polito.mad.g26.playingcourtreservation.util.makeVisible
import it.polito.mad.g26.playingcourtreservation.util.setupActionBar
import it.polito.mad.g26.playingcourtreservation.util.showActionBar
import it.polito.mad.g26.playingcourtreservation.util.toast
import it.polito.mad.g26.playingcourtreservation.viewmodel.EditProfileViewModel
import it.polito.mad.g26.playingcourtreservation.viewmodel.SharedProfileViewModel
import pl.droidsonroids.gif.GifImageView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class EditProfileFragment : Fragment(R.layout.edit_profile_fragment) {

    private val viewModel by viewModels<EditProfileViewModel>()
    private lateinit var sharedProfileViewModel: SharedProfileViewModel

    // Visual Components
    private lateinit var usernameContainer: TextInputLayout
    private lateinit var fullNameContainer: TextInputLayout
    private lateinit var locationContainer: TextInputLayout
    private lateinit var usernameEditText: EditText
    private lateinit var fullNameEditText: EditText
    private lateinit var locationEditText: EditText
    private lateinit var positionAutoComplete: AutoCompleteTextView
    private lateinit var birthDateEditText: EditText
    private lateinit var genderAutoComplete: AutoCompleteTextView
    private lateinit var datePickerDialog: DatePickerDialog
    private val myCalendar = Calendar.getInstance()
    private lateinit var loaderImage: GifImageView
    private lateinit var confirmAlertDialog: AlertDialog
    private lateinit var avatarImage: ImageView
    private var imageUri: Uri? = null
    private lateinit var bitMapImage: Bitmap
    private lateinit var profilePictureAlertDialog: BottomSheetDialog
    private lateinit var sportRecycleView: RecyclerView
    private lateinit var guide: Guideline
    private lateinit var configuration: Configuration
    private lateinit var metrics: DisplayMetrics

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBar(activity, "Edit Profile", true)
        sharedProfileViewModel =
            ViewModelProvider(requireActivity())[SharedProfileViewModel::class.java]
        viewModel.initialize(sharedProfileViewModel.currentUserInfo)

        // Handle top menu actions
        val menuHost: MenuHost = requireActivity()
        handleMenuAction(menuHost)

        // Find visual components
        usernameContainer = view.findViewById(R.id.username_container)
        fullNameContainer = view.findViewById(R.id.full_name_container)
        locationContainer = view.findViewById(R.id.location_container)
        usernameEditText = view.findViewById(R.id.username_et)
        positionAutoComplete = view.findViewById(R.id.position_autocomplete)
        fullNameEditText = view.findViewById(R.id.full_name_et)
        birthDateEditText = view.findViewById(R.id.dob_et)
        genderAutoComplete = view.findViewById(R.id.gender_autocomplete)
        locationEditText = view.findViewById(R.id.location_et)
        avatarImage = view.findViewById(R.id.avatar)
        sportRecycleView = view.findViewById(R.id.editSportsRV)
        loaderImage = view.findViewById(R.id.loaderImage)

        // Load current user information
        val currentUserInformation = viewModel.userInformation
        usernameEditText.setText(currentUserInformation.username)
        positionAutoComplete.setText(currentUserInformation.position ?: "", false)
        fullNameEditText.setText(currentUserInformation.fullname)
        birthDateEditText.setText(currentUserInformation.birthDate ?: "")
        genderAutoComplete.setText(currentUserInformation.gender ?: "", false)
        locationEditText.setText(currentUserInformation.location ?: "")
        sportRecycleView.adapter = UserSkillsAdapter(viewModel.getAllSkills(), true)
        sportRecycleView.layoutManager = LinearLayoutManager(context)

        // Init components
        populateDropdowns()
        setupBirthDateComponents()
        setupValidationHelpers()

        // Handle User Update
        viewModel.updateState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    loaderImage.setFreezesAnimation(false)
                    loaderImage.makeVisible()
                }

                is UiState.Failure -> {
                    loaderImage.makeGone()
                    loaderImage.setFreezesAnimation(true)
                    toast(state.error ?: "Unable to update user information")
                }

                is UiState.Success -> {
                    findNavController().popBackStack()
                    loaderImage.makeGone()
                    loaderImage.setFreezesAnimation(true)
                    toast("Profile successfully updated")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        showActionBar(activity)
        populateDropdowns()
    }

    // Top Bar Management
    private fun handleMenuAction(menuHost: MenuHost) {
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.edit_profile_menu, menu)
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                // Handle the menu selection
                return when (item.itemId) {
                    // Back
                    android.R.id.home -> {
                        findNavController().popBackStack()
                        true
                    }

                    // Confirm Changes
                    R.id.confirm_menu_item -> {
                        val updatedUserInformation = viewModel.userInformation.copy(
                            username = usernameEditText.text.toString(),
                            fullname = fullNameEditText.text.toString(),
                            location = locationEditText.text.toString(),
                            gender = genderAutoComplete.text.toString(),
                            position = positionAutoComplete.text.toString(),
                            birthDate = birthDateEditText.text.toString(),
                            skills = (sportRecycleView.adapter as UserSkillsAdapter).getSkills()
                        )
                        if (areUserInformationValid(updatedUserInformation))
                            viewModel.updateCurrentUserInformation(updatedUserInformation)
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    // Dropdowns Management
    private fun populateDropdowns() {
        populatePositionAutoComplete()
        populateGenderAutoComplete()
    }

    private fun populatePositionAutoComplete() {
        val positionItems = resources.getStringArray(R.array.position_array)
        val adapterPos = ArrayAdapter(requireContext(), R.layout.list_item, positionItems)
        positionAutoComplete.setAdapter(adapterPos)
    }

    private fun populateGenderAutoComplete() {
        val genderItems: Array<String> = resources.getStringArray(R.array.gender_array)
        val adapterGen = ArrayAdapter(requireContext(), R.layout.list_item, genderItems)
        genderAutoComplete.setAdapter(adapterGen)
    }

    // Birth Date Management
    private fun setupBirthDateComponents() {
        val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            myCalendar[Calendar.YEAR] = year
            myCalendar[Calendar.MONTH] = month
            myCalendar[Calendar.DAY_OF_MONTH] = day
            updateBirthDateEditText(myCalendar)
        }
        val maxDate = Calendar.getInstance()
        maxDate.add(Calendar.YEAR, -14)
        datePickerDialog = DatePickerDialog(
            requireContext(),
            datePicker,
            myCalendar[Calendar.YEAR],
            myCalendar[Calendar.MONTH],
            myCalendar[Calendar.DAY_OF_MONTH]
        )
        datePickerDialog.datePicker.maxDate = maxDate.timeInMillis
        birthDateEditText.setOnClickListener {
            datePickerDialog.show()
        }
    }

    private fun updateBirthDateEditText(myCalendar: Calendar) {
        val sdf = SimpleDateFormat(User.BIRTHDATE_PATTERN, Locale.getDefault())
        birthDateEditText.setText(sdf.format(myCalendar.time))
    }

    // Validation Management
    private fun areUserInformationValid(user: User): Boolean {
        if (user.username.isEmpty() || usernameContainer.helperText != null) {
            toast("Please enter a valid username")
            return false
        }

        if (user.position.isNullOrEmpty()) {
            toast("The position field is mandatory")
            return false
        }

        if (user.fullname.isEmpty() || fullNameContainer.helperText != null) {
            toast("Please enter a valid fullname")
            return false
        }

        if (user.birthDate.isNullOrEmpty()) {
            toast("The birth date field is mandatory")
            return false
        }

        if (user.gender.isNullOrEmpty()) {
            toast("The gender field is mandatory")
            return false
        }

        if (user.location.isNullOrEmpty() || locationContainer.helperText != null) {
            toast("Please enter a valid location")
            return false
        }

        return true
    }

    private fun setupValidationHelpers() {
        usernameEditText.addTextChangedListener {
            usernameContainer.helperText = validUsername()
        }

        fullNameEditText.addTextChangedListener {
            fullNameContainer.helperText = validFullName()
        }

        locationEditText.addTextChangedListener {
            locationContainer.helperText = validLocation()
        }
    }

    private fun validUsername(): String? {
        val usernameText = usernameEditText.text.toString()
        val regex = "[A-Za-z]\\w{7,29}".toRegex() // Username from 8 to 30 characters
        return when {
            usernameText.isEmpty()
            -> getString(R.string.required_helper)

            usernameText.length < 8
            -> getString(R.string.username_min_length)

            usernameText.length > 30
            -> getString(R.string.username_max_length)

            !usernameText.matches(regex)
            -> getString(R.string.invalid_field_helper)

            else -> null
        }
    }

    private fun validFullName(): String? {
        val fullNameText = fullNameEditText.text.toString()
        val regex = "([A-Za-z][a-z]*)+([ '\\\\-][A-Za-z]+)*[/.']?".toRegex()
        return when {
            fullNameText.isEmpty()
            -> getString(R.string.required_helper)

            !fullNameText.matches(regex)
            -> getString(R.string.invalid_field_helper)

            else -> null
        }
    }

    private fun validLocation(): String? {
        val locationText = locationEditText.text.toString()
        val regex = "[a-zA-Z]+([ \\-][a-zA-Z]+)*\$".toRegex()
        return when {
            locationText.isEmpty()
            -> getString(R.string.required_helper)

            !locationText.matches(regex)
            -> getString(R.string.invalid_field_helper)

            else -> null
        }
    }

}
