package it.polito.mad.g26.playingcourtreservation.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout
import it.polito.mad.g26.playingcourtreservation.R
import java.io.FileDescriptor
import java.io.IOException
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    //FARE CAMBIO ORIENTAMENTO
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

    private lateinit var avatarImage: ImageView
    private var imageUri: Uri? = null
    private lateinit var bitMapImage: Bitmap

    //get the image from gallery and display it
    private val galleryActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        processResponseGallery(it)
    }
    private fun processResponseGallery(response: ActivityResult){
        if (response.resultCode == RESULT_OK) {
            imageUri = response.data?.data
            val inputImage: Bitmap? = uriToBitmap(imageUri)
            bitMapImage = rotateBitmap(inputImage!!)
            avatarImage.setImageBitmap(bitMapImage)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        // Change Title
        supportActionBar?.setTitle(R.string.edit_show_profile_title)
        // Set Back Button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //username management
        usernameEditText = findViewById(R.id.username_et)
        usernameContainer = findViewById(R.id.username_container)
        usernameEditText.addTextChangedListener {
            usernameContainer.helperText = validUsername()
        }

        //position dropdown management
        val positionItems = resources.getStringArray(R.array.position_array)
        autoCompletePosition = findViewById(R.id.position_autocomplete)
        val adapterPos = ArrayAdapter(this, R.layout.list_item, positionItems)
        autoCompletePosition.setAdapter(adapterPos)
        // Imposto il primo elemento come valore predefinito //MOCK
        if (positionItems.isNotEmpty()) {
            autoCompletePosition.setText(positionItems[0], false)
        }

        //fullName management
        fullNameEditText = findViewById(R.id.fullname_et)
        fullNameContainer = findViewById(R.id.fullname_container)
        fullNameEditText.addTextChangedListener {
            fullNameContainer.helperText = validFullName()
        }

        //Date of birth management
        dateOfBirthEditText = findViewById(R.id.dob_et)
        //imposto data nascita a 21 anni fa
        myCalendar.add(Calendar.YEAR, -21)
        updateDateOfBirthEditText(myCalendar)
        val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, day)
            updateDateOfBirthEditText(myCalendar)
        }
        dateOfBirthEditText.setOnClickListener {
            val dp = DatePickerDialog(
                this,
                datePicker,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            //inserisco che utente deve avere almeno 14 anni
            val maxDate = Calendar.getInstance()
            maxDate.add(Calendar.YEAR, -14)
            dp.datePicker.maxDate = maxDate.timeInMillis
            dp.show()
        }

        //gender dropdown management
        val genderItems = resources.getStringArray(R.array.gender_array)
        autoCompleteGender = findViewById(R.id.gender_autocomplete)
        val adapterGen = ArrayAdapter(this, R.layout.list_item, genderItems)
        autoCompleteGender.setAdapter(adapterGen)
        // Imposto il primo elemento come valore predefinito. MOCK
        if (genderItems.isNotEmpty()) {
            autoCompleteGender.setText(genderItems[0], false)
        }

        //location management
        locationEditText = findViewById(R.id.location_et)
        locationContainer = findViewById(R.id.location_container)
        locationEditText.addTextChangedListener {
            locationContainer.helperText = validLocation()
        }

        avatarImage = findViewById(R.id.avatar)
        val editBtn = findViewById<ImageButton>(R.id.imageButton)

        editBtn.setOnClickListener {
            val alertCustomDialog = LayoutInflater.from(this).inflate(R.layout.custom_dialog_photo, null)
            val builder = AlertDialog.Builder(this)
            builder.setView(alertCustomDialog)
            val galleryBtn = alertCustomDialog.findViewById<ImageButton>(R.id.gallery)
            val cameraBtn = alertCustomDialog.findViewById<ImageButton>(R.id.camera)
            // Create the AlertDialog
            val alertDialog: AlertDialog = builder.create()
            // Set other dialog properties
            alertDialog.setCancelable(true)
            alertDialog.show()

            galleryBtn.setOnClickListener{
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                galleryActivityResultLauncher.launch(galleryIntent)
                alertDialog.dismiss()
            }
            cameraBtn.setOnClickListener{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                        checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                    {
                        val permission = arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                        requestPermissions(permission, 112)
                    } else {
                        openCamera()
                    }
                } else {
                    openCamera()
                }
                alertDialog.dismiss()
            }
        }
    }

    private fun validUsername(): String? {
        val usernameText = usernameEditText.text.toString()
        val regex =
            "[A-Za-z]\\w{7,29}".toRegex() // Validazione. Username da 8 a 30 caratteri
        return when {
            usernameText.isEmpty()
            -> getString(R.string.required_helper)
            usernameText.length < 8
            -> "Username min length is 8 characters"
            usernameText.length > 30
            -> "Username max length is 30 characters"
            !usernameText.matches(regex)
            -> getString(R.string.invalid_field_helper)
            else -> null
        }
    }

    private fun validFullName(): String? {
        val fullNameText = fullNameEditText.text.toString()
        val regex =
            "([A-Za-z][a-z]*)+([ '\\\\-][A-Za-z]+)*[/.']?".toRegex() // Validazione
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
            "[a-zA-Z]+([ \\-][a-zA-Z]+)*\$".toRegex() // Validazione
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
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.ITALY)
        dateOfBirthEditText.setText(sdf.format(myCalendar.time))
    }

    private fun submitForm() {
        val validUsername = usernameContainer.helperText == null
        val validFullName = fullNameContainer.helperText == null
        val validLocation = locationContainer.helperText == null
        var errorMessage: String = ""
        if (validLocation && validFullName && validUsername) {
            AlertDialog.Builder(this).setTitle(getString(R.string.edit_profile_ok_update_dialog_title))
                .setMessage(getString(R.string.edit_profile_ok_update_dialog_message)).setPositiveButton("Ok") { _, _ ->
                }.setOnDismissListener {
                    //X PRENDERE I VALORI:
//                  usernameEditText.text.toString()
//                    fullNameEditText.text.toString()
//                   locationEditText.text.toString()
//                   dateOfBirthEditText.text.toString()
//                      autoCompleteGender.text.toString()
//                      autoCompletePosition.text.toString()
                    finish()
                }.show()

        } else {
            AlertDialog.Builder(this).setTitle(getString(R.string.edit_profile_no_ok_update_dialog_title))
                .setMessage(getString(R.string.edit_profile_no_ok_update_dialog_message))
                .setPositiveButton("Modify") { _, _ ->
                }.show()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_profile_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            // Back
            android.R.id.home -> {
                finish()
                true
            }
            // Confirm Changes
            R.id.confirm_menu_item -> {
                // TODO: save changes
                submitForm()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        //salvo calendario
        outState.putInt("YEAR", myCalendar.get(Calendar.YEAR))
        outState.putInt("MONTH", myCalendar.get(Calendar.MONTH))
        outState.putInt("DAY_OF_MONTH", myCalendar.get(Calendar.DAY_OF_MONTH))
        //salvo avatar image
        outState.putString("imageUri", imageUri.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        //recupero calendario
        myCalendar.set(Calendar.YEAR, savedInstanceState.getInt("YEAR"))
        myCalendar.set(Calendar.MONTH, savedInstanceState.getInt("MONTH"))
        myCalendar.set(Calendar.DAY_OF_MONTH, savedInstanceState.getInt("DAY_OF_MONTH"))

        //ripristino dropdown position
        val positionItems = resources.getStringArray(R.array.position_array)
        val adapterPos = ArrayAdapter(this, R.layout.list_item, positionItems)
        autoCompletePosition.setAdapter(adapterPos)

        //ripristino dropdown gender
        val genderItems = resources.getStringArray(R.array.gender_array)
        val adapterGender = ArrayAdapter(this, R.layout.list_item, genderItems)
        autoCompleteGender.setAdapter(adapterGender)

        //ripristino avatar image
        imageUri = Uri.parse(savedInstanceState.getString("imageUri"))
        val inputImage: Bitmap? = uriToBitmap(imageUri)
        bitMapImage = rotateBitmap(inputImage!!)
        avatarImage.setImageBitmap(bitMapImage)


    }

    //opens camera so that user can capture image
    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraActivityResultLauncher.launch(cameraIntent)
    }

    private val cameraActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        processResponseCamera(it)
    }

    private fun processResponseCamera(response: ActivityResult){
        if (response.resultCode == RESULT_OK) {
            val inputImage: Bitmap? = uriToBitmap(imageUri)
            bitMapImage = rotateBitmap(inputImage!!)
            avatarImage.setImageBitmap(bitMapImage)
        }
    }

    //takes URI of the image and returns bitmap
    private fun uriToBitmap(selectedFileUri: Uri?): Bitmap? {
        try {
            val parcelFileDescriptor: ParcelFileDescriptor? = contentResolver.openFileDescriptor(selectedFileUri!!, "r")
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
        val cur: Cursor? = contentResolver.query(imageUri!!, orientationColumn, null, null, null)
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