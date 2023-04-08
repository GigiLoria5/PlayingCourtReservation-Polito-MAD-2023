package it.polito.mad.g26.playingcourtreservation.activity

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout
import it.polito.mad.g26.playingcourtreservation.R
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


    }
}