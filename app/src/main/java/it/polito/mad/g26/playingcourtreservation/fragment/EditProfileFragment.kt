package it.polito.mad.g26.playingcourtreservation.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.Guideline
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.adapter.EditProfileAdapter
import it.polito.mad.g26.playingcourtreservation.newModel.Reservation
import it.polito.mad.g26.playingcourtreservation.util.setupActionBar
import it.polito.mad.g26.playingcourtreservation.util.showActionBar
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.FileDescriptor
import java.io.IOException
import java.util.*

@AndroidEntryPoint
class EditProfileFragment : Fragment(R.layout.edit_profile_fragment) {

    private lateinit var usernameEditText: EditText
    private lateinit var usernameContainer: TextInputLayout

    private lateinit var fullNameEditText: EditText
    private lateinit var fullNameContainer: TextInputLayout

    private lateinit var locationEditText: EditText
    private lateinit var locationContainer: TextInputLayout

    private lateinit var autoCompletePosition: AutoCompleteTextView
    private lateinit var dateOfBirthEditText: EditText
    private lateinit var autoCompleteGender: AutoCompleteTextView

    private val myCalendar = Calendar.getInstance()
    private lateinit var datePickerDialog: DatePickerDialog

    private lateinit var confirmAlertDialog: AlertDialog

    private lateinit var avatarImage: ImageView
    private var imageUri: Uri? = null
    private lateinit var bitMapImage: Bitmap
    private lateinit var profilePictureAlertDialog: BottomSheetDialog

    private lateinit var sportRecycleView: RecyclerView
    private lateinit var sportList: List<String>
    private lateinit var sportRating: MutableList<Float>
    private lateinit var guide: Guideline
    private lateinit var configuration: Configuration
    private lateinit var metrics: DisplayMetrics


    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.CAMERA] == true && (android.os.Build.VERSION.SDK_INT > 29 || permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true)) {
                openCamera()
            }
        }

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // save current calendar selection
        outState.putInt("YEAR", myCalendar[Calendar.YEAR])
        outState.putInt("MONTH", myCalendar[Calendar.MONTH])
        outState.putInt("DAY_OF_MONTH", myCalendar[Calendar.DAY_OF_MONTH])
        //save datePickerDialog
        if (::datePickerDialog.isInitialized && datePickerDialog.isShowing) {
            outState.putBoolean("datePickerDialogShowing", true)
            //save selected date
            outState.putInt("YEAR_SEL", datePickerDialog.datePicker.year)
            outState.putInt("MONTH_SEL", datePickerDialog.datePicker.month)
            outState.putInt("DAY_OF_MONTH_SEL", datePickerDialog.datePicker.dayOfMonth)
            datePickerDialog.dismiss()
        } else
            outState.putBoolean("datePickerDialogShowing", false)

        // save avatar image
        if (imageUri != null) {
            outState.putString("imageUri", imageUri.toString())
        } else {
            outState.putString("imageUri", "null")
        }

        // save profilePictureAlertDialog
        if (::profilePictureAlertDialog.isInitialized && profilePictureAlertDialog.isShowing) {
            outState.putBoolean("profilePictureAlertDialogShowing", true)
            profilePictureAlertDialog.dismiss()
        } else
            outState.putBoolean("profilePictureAlertDialogShowing", false)

        // save confirmAlertDialog
        if (::confirmAlertDialog.isInitialized &&
            confirmAlertDialog.isShowing
        ) {
            outState.putBoolean("confirmAlertDialogShowing", true)
            confirmAlertDialog.dismiss()
        } else
            outState.putBoolean("confirmAlertDialogShowing", false)

        //save rating
        if (::sportRating.isInitialized)
            outState.putFloatArray("rating", sportRating.toFloatArray())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBar(activity, "Edit Profile", true)

        usernameEditText = view.findViewById(R.id.username_et)
        autoCompletePosition = view.findViewById(R.id.position_autocomplete)
        fullNameEditText = view.findViewById(R.id.full_name_et)
        dateOfBirthEditText = view.findViewById(R.id.dob_et)
        autoCompleteGender = view.findViewById(R.id.gender_autocomplete)
        locationEditText = view.findViewById(R.id.location_et)
        avatarImage = view.findViewById(R.id.avatar)

        usernameContainer = view.findViewById(R.id.username_container)
        fullNameContainer = view.findViewById(R.id.full_name_container)
        locationContainer = view.findViewById(R.id.location_container)
        sportRecycleView = view.findViewById(R.id.edit_profile_recycler_view)
        sportList = resources.getStringArray(R.array.sport_array).toList()

        guide = requireView().findViewById(R.id.guideline)
        metrics = requireContext().resources.displayMetrics
        // Get the current configuration
        configuration = resources.configuration
        // Check if the orientation is landscape
        if (configuration.orientation != Configuration.ORIENTATION_LANDSCAPE) {
            // The layout is in portrait mode
            val height = metrics.heightPixels
            val pixelsLimit = (height / 100) * 33
            guide.setGuidelineBegin(pixelsLimit)
        }

        //PERSISTENCE
        val sharedPref = this.requireActivity().getSharedPreferences("test", Context.MODE_PRIVATE)

        if (sharedPref.contains("profile")) {//work to replace all the strings
            val json = sharedPref.getString("profile", "Default")?.let { JSONObject(it) }
            usernameEditText.setText(json?.getString("username"))
            autoCompletePosition.setText(json?.getString("position"), false)
            fullNameEditText.setText(json?.getString("fullName"))
            dateOfBirthEditText.setText(json?.getString("date"))
            autoCompleteGender.setText(json?.getString("gender"), false)
            locationEditText.setText(json?.getString("location"))
            json?.getInt("year")?.let { myCalendar[Calendar.YEAR] = it }
            json?.getInt("month")?.let { myCalendar[Calendar.MONTH] = it }
            json?.getInt("day")?.let { myCalendar[Calendar.DAY_OF_MONTH] = it }
            if (json?.getString("rating") != null) {
                //retrieve rating from json
                val sublist = json.getString("rating").split(",")
                //transform in float
                sportRating = mutableListOf()
                for (string in sublist)
                    sportRating.add(string.toFloat())
                sportRecycleView.adapter = EditProfileAdapter(sportList, sportRating)
                sportRecycleView.layoutManager =
                    LinearLayoutManager(context)
            }
        } else {//put the default value
            usernameEditText.setText(getString(R.string.default_username))
            autoCompletePosition.setText(getString(R.string.default_position), false)
            fullNameEditText.setText(getString(R.string.default_full_name))
            dateOfBirthEditText.setText(getString(R.string.default_date))
            autoCompleteGender.setText(getString(R.string.default_gender), false)
            locationEditText.setText(getString(R.string.default_location))
            myCalendar.add(Calendar.YEAR, -21)
            //RecyclerView Management
            sportRating = MutableList(sportList.size) { 0f }
            sportRecycleView.adapter = EditProfileAdapter(sportList, sportRating)
            sportRecycleView.layoutManager =
                LinearLayoutManager(context)
        }
        //position dropdown management
        val positionItems = resources.getStringArray(R.array.position_array)
        val adapterPos = ArrayAdapter(requireContext(), R.layout.list_item, positionItems)
        autoCompletePosition.setAdapter(adapterPos)

        //gender dropdown management
        val genderItems: Array<String> = resources.getStringArray(R.array.gender_array)
        val adapterGen = ArrayAdapter(requireContext(), R.layout.list_item, genderItems)
        autoCompleteGender.setAdapter(adapterGen)

        //Date of birth management
        //updateDateOfBirthEditText(myCalendar)
        val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            myCalendar[Calendar.YEAR] = year
            myCalendar[Calendar.MONTH] = month
            myCalendar[Calendar.DAY_OF_MONTH] = day
            updateDateOfBirthEditText(myCalendar)
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

        //image management
        val editBtn = view.findViewById<ImageButton>(R.id.imageButton)

        profilePictureAlertDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        profilePictureAlertDialog.setContentView(R.layout.edit_profile_custom_dialog_photo)
        profilePictureAlertDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        val galleryBtn = profilePictureAlertDialog.findViewById<ImageButton>(R.id.gallery)
        val cameraBtn = profilePictureAlertDialog.findViewById<ImageButton>(R.id.camera)
        // Create the AlertDialog
        // Set other dialog properties
        profilePictureAlertDialog.setCancelable(true)

        //Restore status
        if (savedInstanceState !== null) {
            //take calendar
            myCalendar[Calendar.YEAR] = savedInstanceState.getInt("YEAR")
            myCalendar[Calendar.MONTH] = savedInstanceState.getInt("MONTH")
            myCalendar[Calendar.DAY_OF_MONTH] = savedInstanceState.getInt("DAY_OF_MONTH")

            //restore datePickerDialog
            val datePickerDialogOn =
                savedInstanceState.getBoolean("datePickerDialogShowing")
            if (datePickerDialogOn) {
                datePickerDialog.updateDate(
                    savedInstanceState.getInt("YEAR_SEL"),
                    savedInstanceState.getInt("MONTH_SEL"),
                    savedInstanceState.getInt("DAY_OF_MONTH_SEL")
                )
                datePickerDialog.show()
            }

            //restore avatar image
            if (savedInstanceState.getString("imageUri") != "null") {
                imageUri = Uri.parse(savedInstanceState.getString("imageUri"))
                val inputImage: Bitmap? = uriToBitmap(imageUri)
                bitMapImage = rotateBitmap(inputImage!!)
                avatarImage.setImageBitmap(bitMapImage)
            }

            //restore profilePictureAlertDialog
            val profilePictureAlertOn =
                savedInstanceState.getBoolean("profilePictureAlertDialogShowing")
            if (profilePictureAlertOn)
                profilePictureAlertDialog.show()

            //restore confirmAlertDialog
            val confirmAlertOn = savedInstanceState.getBoolean("confirmAlertDialogShowing")
            if (confirmAlertOn)
                submitForm()

            sportRating = savedInstanceState.getFloatArray("rating")!!.toMutableList()
            sportRecycleView.adapter = EditProfileAdapter(sportList, sportRating)
        }


        //IMAGE MANAGEMENT
        if (imageUri == null) {
            val file = requireActivity().applicationContext.getFileStreamPath("imageBit")
            if (file.exists()) {
                val fileInput = requireActivity().openFileInput("imageBit")
                bitMapImage = if (fileInput.available() > 0) {
                    val bitmap = BitmapFactory.decodeStream(fileInput)//already decompressed
                    avatarImage.setImageBitmap(bitmap)
                    bitmap
                } else {
                    avatarImage.setImageBitmap(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.profile_default
                        )!!.toBitmap()
                    )
                    avatarImage.drawable.toBitmap()
                }
                fileInput.close()
            } else {
                avatarImage.setImageBitmap(
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.profile_default
                    )!!.toBitmap()
                )
                bitMapImage = avatarImage.drawable.toBitmap()
            }
        }

        //username management
        usernameEditText.addTextChangedListener {
            usernameContainer.helperText = validUsername()
        }

        //fullName management
        fullNameEditText.addTextChangedListener {
            fullNameContainer.helperText = validFullName()
        }

        dateOfBirthEditText.setOnClickListener {
            datePickerDialog.show()
        }


        //location management
        locationEditText.addTextChangedListener {
            locationContainer.helperText = validLocation()
        }

        galleryBtn?.setOnClickListener {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

            galleryActivityResultLauncher.launch(galleryIntent)
            profilePictureAlertDialog.dismiss()
        }

        cameraBtn?.setOnClickListener {
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

        editBtn.setOnClickListener {
            profilePictureAlertDialog.show()
        }

        val menuHost: MenuHost = requireActivity()
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

                        //Save new profile
                        val sharedPreferences =
                            requireActivity().getSharedPreferences("test", Context.MODE_PRIVATE)
                        val json = JSONObject()
                        json.put("username", usernameEditText.text.toString())
                        json.put("fullName", fullNameEditText.text.toString())
                        json.put("location", locationEditText.text.toString())
                        json.put("gender", autoCompleteGender.text.toString())
                        json.put("position", autoCompletePosition.text.toString())
                        json.put("date", dateOfBirthEditText.text.toString())
                        json.put("year", myCalendar[Calendar.YEAR])
                        json.put("month", myCalendar[Calendar.MONTH])
                        json.put("day", myCalendar[Calendar.DAY_OF_MONTH])

                        //save rating
                        var rating = ""
                        for (i in sportRating)
                            rating = "$rating,$i"
                        val finalRating = rating.substring(1)
                        json.put("rating", finalRating)

                        //Calculate and save age
                        val year = myCalendar[Calendar.YEAR]
                        val day = myCalendar[Calendar.DAY_OF_YEAR]
                        val todayCalendar = Calendar.getInstance(TimeZone.getDefault())
                        val currentYear = todayCalendar[Calendar.YEAR]
                        var age = currentYear - year
                        val currentDay = todayCalendar[Calendar.DAY_OF_YEAR]
                        if (day > currentDay) {
                            age--
                        }
                        json.put("age", age)

                        val editor = sharedPreferences.edit()
                        editor.putString("profile", json.toString())
                        editor.apply()

                        //Saving image
                        val stream = ByteArrayOutputStream()
                        bitMapImage.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                        val byteArray = stream.toByteArray()
                        val fileOut =
                            requireActivity().openFileOutput("imageBit", Context.MODE_PRIVATE)
                        fileOut.write(byteArray)
                        fileOut.close()

                        submitForm()

                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onResume() {
        super.onResume()
        showActionBar(activity)
        //position dropdown management
        val positionItems = resources.getStringArray(R.array.position_array)
        val adapterPos = ArrayAdapter(requireContext(), R.layout.list_item, positionItems)
        autoCompletePosition.setAdapter(adapterPos)
        //gender dropdown management
        val genderItems: Array<String> = resources.getStringArray(R.array.gender_array)
        val adapterGen = ArrayAdapter(requireContext(), R.layout.list_item, genderItems)
        autoCompleteGender.setAdapter(adapterGen)
    }

    private fun validUsername(): String? {
        val usernameText = usernameEditText.text.toString()
        val regex =
            "[A-Za-z]\\w{7,29}".toRegex() // Validation. Username da 8 a 30 characters
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
        val regex =
            "([A-Za-z][a-z]*)+([ '\\\\-][A-Za-z]+)*[/.']?".toRegex() // Validation
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
        val regex =
            "[a-zA-Z]+([ \\-][a-zA-Z]+)*\$".toRegex() // Validation
        return when {
            locationText.isEmpty()
            -> getString(R.string.required_helper)

            !locationText.matches(regex)
            -> getString(R.string.invalid_field_helper)

            else -> null
        }
    }

    //update Date Of Birth Edit Text
    private fun updateDateOfBirthEditText(myCalendar: Calendar) {
        val myFormat = Reservation.getDatePattern()
        val sdf = SimpleDateFormat(myFormat, Locale.ITALY)
        dateOfBirthEditText.setText(sdf.format(myCalendar.time))
    }

    private fun validInputData(): Boolean {
        val validUsername = usernameContainer.helperText == null
        val validFullName = fullNameContainer.helperText == null
        val validLocation = locationContainer.helperText == null
        return validLocation && validFullName && validUsername
    }

    private fun confirmAlertDialogBuilder(isOk: Boolean) {
        return if (isOk) {
            findNavController().popBackStack()
            Toast.makeText(
                context,
                R.string.edit_profile_ok_update_dialog_message,
                Toast.LENGTH_SHORT
            ).show()

        } else {
            Toast.makeText(
                context,
                R.string.edit_profile_no_ok_update_dialog_message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun submitForm() {
        confirmAlertDialogBuilder(validInputData())

    }

    //opens camera so that user can capture image
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

    //takes URI of the image and returns bitmap
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

    //rotate image if image captured on samsung devices
    //Most phone cameras are landscape, meaning if you take the photo in portrait, the resulting photos will be rotated 90 degrees.
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
        Log.d("tryOrientation", orientation.toString() + "")
        val rotationMatrix = Matrix()
        rotationMatrix.setRotate(orientation.toFloat())
        return Bitmap.createBitmap(input, 0, 0, input.width, input.height, rotationMatrix, true)
    }
}