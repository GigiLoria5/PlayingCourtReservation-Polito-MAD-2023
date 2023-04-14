package it.polito.mad.g26.playingcourtreservation.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
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
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.ui.CustomTextView
import org.json.JSONObject
import java.io.ByteArrayOutputStream
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
    private lateinit var alertDialog: AlertDialog

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

        //PERSISTENCE
        val sharedPref = getSharedPreferences("test", Context.MODE_PRIVATE)

        usernameEditText = findViewById(R.id.username_et)
        autoCompletePosition = findViewById(R.id.position_autocomplete)
        fullNameEditText = findViewById(R.id.fullname_et)
        dateOfBirthEditText = findViewById(R.id.dob_et)
        autoCompleteGender = findViewById(R.id.gender_autocomplete)
        locationEditText = findViewById(R.id.location_et)
        avatarImage = findViewById(R.id.avatar)

        if(sharedPref.contains("profile")){//work to replace all the strings
            val json= JSONObject(sharedPref.getString("profile","Default"))
            usernameEditText.setText(json.getString("username"))
            autoCompletePosition.setText(json.getString("position"),false)
            fullNameEditText.setText(json.getString("fullName"))
            dateOfBirthEditText.setText(json.getString("date"))
            autoCompleteGender.setText(json.getString("gender"), false)
            locationEditText.setText(json.getString("location"))
            myCalendar.set(Calendar.YEAR, json.getInt("year"))
            myCalendar.set(Calendar.MONTH, json.getInt("month"))
            myCalendar.set(Calendar.DAY_OF_MONTH, json.getInt("day"))


        }else{//put the default value
            usernameEditText.setText(getString(R.string.default_username))
            autoCompletePosition.setText(getString(R.string.default_position), false)
            fullNameEditText.setText(getString(R.string.default_fullname))
            dateOfBirthEditText.setText(getString(R.string.default_date))
            autoCompleteGender.setText(getString(R.string.default_gender), false)
            locationEditText.setText(getString(R.string.default_location))
            myCalendar.add(Calendar.YEAR, -21)
        }

        //IMAGE MANAGEMENT
        val file = applicationContext.getFileStreamPath("imageBit")
        if(file.exists()){
            val fileInput= openFileInput("imageBit")
            if(fileInput.available()>0){
                val bitmap= BitmapFactory.decodeStream(fileInput)//already decompressed
                avatarImage.setImageBitmap(bitmap)
                bitMapImage=bitmap
            }else{
                avatarImage.setImageBitmap(AppCompatResources.getDrawable(this,R.drawable.profile_default)!!.toBitmap())
                bitMapImage= avatarImage.drawable.toBitmap()
            }
            fileInput.close()
        }else{
            avatarImage.setImageBitmap(AppCompatResources.getDrawable(this,R.drawable.profile_default)!!.toBitmap())
            bitMapImage= avatarImage.drawable.toBitmap()
        }


        //username management
        usernameContainer = findViewById(R.id.username_container)
        usernameEditText.addTextChangedListener {
            usernameContainer.helperText = validUsername()
        }

        //position dropdown management
        val positionItems = resources.getStringArray(R.array.position_array)
        val adapterPos = ArrayAdapter(this, R.layout.list_item, positionItems)
        autoCompletePosition.setAdapter(adapterPos)

        //fullName management
        fullNameContainer = findViewById(R.id.fullname_container)
        fullNameEditText.addTextChangedListener {
            fullNameContainer.helperText = validFullName()
        }

        //Date of birth management
        //imposto data nascita a 21 anni fa
        //myCalendar.add(Calendar.YEAR, -21)
        //updateDateOfBirthEditText(myCalendar)
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
        val adapterGen = ArrayAdapter(this, R.layout.list_item, genderItems)
        autoCompleteGender.setAdapter(adapterGen)

        //location management
        locationContainer = findViewById(R.id.location_container)
        locationEditText.addTextChangedListener {
            locationContainer.helperText = validLocation()
        }

        //image management
        val editBtn = findViewById<ImageButton>(R.id.imageButton)

        val alertCustomDialog = LayoutInflater.from(this).inflate(R.layout.custom_dialog_photo, null)
        var builder = AlertDialog.Builder(this)
        builder.setView(alertCustomDialog)
        val galleryBtn = alertCustomDialog.findViewById<ImageButton>(R.id.gallery)
        val cameraBtn = alertCustomDialog.findViewById<ImageButton>(R.id.camera)
        // Create the AlertDialog
        alertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(true)

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

        editBtn.setOnClickListener {
            alertDialog.show()
        }
    }

    private fun validUsername(): String? {
        val usernameText = usernameEditText.text.toString()
        val regex =
            "[A-Za-z]\\w{7,29}".toRegex() // Validation. Username da 8 a 30 characters
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

                //Save new profile
                val sharedPref = getSharedPreferences("test", Context.MODE_PRIVATE)
                val json=JSONObject()
                json.put("username",usernameEditText.text.toString())
                json.put( "fullName",fullNameEditText.text.toString())
                json.put("location", locationEditText.text.toString())
                json.put("gender",autoCompleteGender.text.toString())
                json.put("position",autoCompletePosition.text.toString())
                json.put("date", dateOfBirthEditText.text.toString())
                json.put("year",myCalendar.get(Calendar.YEAR))
                json.put("month",myCalendar.get(Calendar.MONTH))
                json.put("day",myCalendar.get(Calendar.DAY_OF_MONTH))

                //Calculate and save age
                val year=myCalendar.get(Calendar.YEAR)
                val day=myCalendar.get(Calendar.DAY_OF_YEAR)
                val todayCalendar = Calendar.getInstance(TimeZone.getDefault())
                val currentYear=todayCalendar.get(Calendar.YEAR)
                var age=currentYear-year
                val currentDay=todayCalendar.get(Calendar.DAY_OF_YEAR)
                if(day>currentDay){
                    age--
                }
                json.put("age", age)

                val editor= sharedPref.edit()
                editor.putString("profile", json.toString())
                editor.apply()

                //Saving image
                val stream = ByteArrayOutputStream()
                bitMapImage.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                val byteArray = stream.toByteArray()
                val fileOut= applicationContext.openFileOutput("imageBit", Context.MODE_PRIVATE)
                fileOut.write(byteArray)
                fileOut.close()

                submitForm()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        //save calendar
        outState.putInt("YEAR", myCalendar.get(Calendar.YEAR))
        outState.putInt("MONTH", myCalendar.get(Calendar.MONTH))
        outState.putInt("DAY_OF_MONTH", myCalendar.get(Calendar.DAY_OF_MONTH))
        //save avatar image
        if (imageUri != null){
            outState.putString("imageUri", imageUri.toString())
        }else{
            outState.putString("imageUri", "null")
        }
        if(alertDialog.isShowing){
            alertDialog.dismiss()
        }

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        //take calendar
        myCalendar.set(Calendar.YEAR, savedInstanceState.getInt("YEAR"))
        myCalendar.set(Calendar.MONTH, savedInstanceState.getInt("MONTH"))
        myCalendar.set(Calendar.DAY_OF_MONTH, savedInstanceState.getInt("DAY_OF_MONTH"))

        //restore dropdown position
        val positionItems = resources.getStringArray(R.array.position_array)
        val adapterPos = ArrayAdapter(this, R.layout.list_item, positionItems)
        autoCompletePosition.setAdapter(adapterPos)

        //restore dropdown gender
        val genderItems = resources.getStringArray(R.array.gender_array)
        val adapterGender = ArrayAdapter(this, R.layout.list_item, genderItems)
        autoCompleteGender.setAdapter(adapterGender)

        //restore avatar image
        if (savedInstanceState.getString("imageUri") != "null"){
            imageUri = Uri.parse(savedInstanceState.getString("imageUri"))
            val inputImage: Bitmap? = uriToBitmap(imageUri)
            bitMapImage = rotateBitmap(inputImage!!)
            avatarImage.setImageBitmap(bitMapImage)
        }

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