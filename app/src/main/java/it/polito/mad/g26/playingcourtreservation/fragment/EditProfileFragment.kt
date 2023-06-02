package it.polito.mad.g26.playingcourtreservation.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
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
import java.io.FileDescriptor
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class EditProfileFragment : Fragment(R.layout.edit_profile_fragment) {

    private val viewModel by viewModels<EditProfileViewModel>()
    private lateinit var sharedProfileViewModel: SharedProfileViewModel

    // Visual Components
    private lateinit var loaderImage: GifImageView
    private lateinit var avatarImage: ImageView
    private lateinit var usernameContainer: TextInputLayout
    private lateinit var fullNameContainer: TextInputLayout
    private lateinit var locationContainer: TextInputLayout
    private lateinit var usernameEditText: EditText
    private lateinit var fullNameEditText: EditText
    private lateinit var locationEditText: EditText
    private lateinit var birthDateEditText: EditText
    private lateinit var positionAutoComplete: AutoCompleteTextView
    private lateinit var genderAutoComplete: AutoCompleteTextView
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var sportRecycleView: RecyclerView
    private lateinit var changeImageButton: ImageButton
    private lateinit var galleryBtn: ImageButton
    private lateinit var cameraBtn: ImageButton
    private lateinit var profilePictureAlertDialog: BottomSheetDialog

    private lateinit var bitMapImage: Bitmap
    private val myCalendar = Calendar.getInstance()
    private var imageUri: Uri? = null

    companion object {
        const val IMAGE_URI_KEY = "imageUri"
        const val NO_IMAGE_VALUE = "null"
    }

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
        changeImageButton = view.findViewById(R.id.imageButton)

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
        setupChangeImageManagement()

        //restore avatar image
        if (savedInstanceState !== null && savedInstanceState.getString(IMAGE_URI_KEY) != NO_IMAGE_VALUE) {
            imageUri = Uri.parse(savedInstanceState.getString(IMAGE_URI_KEY))
            val inputImage: Bitmap? = uriToBitmap(imageUri)
            bitMapImage = rotateBitmap(inputImage!!)
            avatarImage.setImageBitmap(bitMapImage)
        }

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

    // Change Image Management
    private fun setupChangeImageManagement() {
        profilePictureAlertDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        profilePictureAlertDialog.setContentView(R.layout.edit_profile_custom_dialog_photo)
        profilePictureAlertDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        galleryBtn = profilePictureAlertDialog.findViewById(R.id.gallery)!!
        cameraBtn = profilePictureAlertDialog.findViewById(R.id.camera)!!
        profilePictureAlertDialog.setCancelable(true)
        // Open Dialog
        changeImageButton.setOnClickListener {
            profilePictureAlertDialog.show()
        }
        // Choose Image from Gallery
        galleryBtn.setOnClickListener {
            val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            galleryActivityResultLauncher.launch(galleryIntent)
            profilePictureAlertDialog.dismiss()
        }
        // Take a Photo
        cameraBtn.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_DENIED ||
                (android.os.Build.VERSION.SDK_INT < 30 &&
                        ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_DENIED)
            ) {
                val permission = arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                requestPermissionLauncher.launch(permission)
            } else {
                openCamera()
            }
            profilePictureAlertDialog.dismiss()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // save avatar image
        if (imageUri != null) {
            outState.putString(IMAGE_URI_KEY, imageUri.toString())
        } else {
            outState.putString(IMAGE_URI_KEY, NO_IMAGE_VALUE)
        }
    }

    // GALLERY UTILS
    // get the image from gallery and display it
    private val galleryActivityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            processResponseGallery(it)
        }

    private fun processResponseGallery(response: ActivityResult) {
        if (response.resultCode == RESULT_OK) {
            imageUri = response.data?.data
            val inputImage: Bitmap? = uriToBitmap(imageUri)
            bitMapImage = rotateBitmap(inputImage!!)
            avatarImage.setImageBitmap(bitMapImage)
        }
    }

    // takes URI of the image and returns bitmap
    private fun uriToBitmap(selectedFileUri: Uri?): Bitmap? {
        try {
            val parcelFileDescriptor: ParcelFileDescriptor? =
                requireActivity().contentResolver.openFileDescriptor(selectedFileUri!!, "r")
            val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image: Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return image
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    // rotate image if image captured on samsung devices
    // Most phone cameras are landscape, meaning if you take the photo in portrait, the resulting photos will be rotated 90 degrees.
    @SuppressLint("Range")
    fun rotateBitmap(input: Bitmap): Bitmap {
        val orientationColumn = arrayOf(MediaStore.Images.Media.ORIENTATION)
        val cur: Cursor? =
            requireActivity().contentResolver.query(imageUri!!, orientationColumn, null, null, null)
        var orientation = -1
        if (cur != null && cur.moveToFirst()) {
            orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]))
            cur.close()
        }
        val rotationMatrix = Matrix()
        rotationMatrix.setRotate(orientation.toFloat())
        return Bitmap.createBitmap(input, 0, 0, input.width, input.height, rotationMatrix, true)
    }

    // CAMERA UTILS
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.CAMERA] == true && (android.os.Build.VERSION.SDK_INT > 29 || permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true)) {
                openCamera()
            }
        }

    // opens camera so that user can capture image
    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        imageUri = requireActivity().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraActivityResultLauncher.launch(cameraIntent)
    }

    private val cameraActivityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            processResponseCamera(it)
        }

    private fun processResponseCamera(response: ActivityResult) {
        if (response.resultCode == RESULT_OK) {
            val inputImage: Bitmap? = uriToBitmap(imageUri)
            bitMapImage = rotateBitmap(inputImage!!)
            avatarImage.setImageBitmap(bitMapImage)
        } else {
            requireActivity().contentResolver.delete(imageUri!!, null, null)
        }
    }

}
