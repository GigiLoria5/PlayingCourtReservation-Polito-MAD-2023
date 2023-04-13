package it.polito.mad.g26.playingcourtreservation.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.TooltipCompat
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.ui.CustomTextView
import org.json.JSONObject

class ShowProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile)

        // Change Title
        supportActionBar?.setTitle(R.string.activity_show_profile_title)

        // Add Tooltips
        val warningIcon = findViewById<ImageView>(R.id.warning_icon)
        TooltipCompat.setTooltipText(warningIcon, getString(R.string.warning_icon_tooltip))
        val dangerIcon = findViewById<ImageView>(R.id.danger_icon)
        TooltipCompat.setTooltipText(dangerIcon, getString(R.string.danger_icon_tooltip))

        //Persistence
        val sharedPref = getSharedPreferences("test", Context.MODE_PRIVATE)

        //Replace if a profile exists
        if(sharedPref.contains("profile")){
            val json= JSONObject(sharedPref.getString("profile","Default"))
            val username =findViewById<TextView>(R.id.username)
            username.text= json.getString("username")
            val position=findViewById<TextView>(R.id.position)
            position.text=json.getString("position")
            val age=findViewById<CustomTextView>(R.id.age).findViewById<TextView>(R.id.value)
            age.text=json.getString("age")
            val gender=findViewById<CustomTextView>(R.id.gender).findViewById<TextView>(R.id.value)
            gender.text=json.getString("gender")
            val fullName=findViewById<CustomTextView>(R.id.fullname).findViewById<TextView>(R.id.value)
            fullName.text=json.getString("fullName")
            val location=findViewById<CustomTextView>(R.id.location).findViewById<TextView>(R.id.value)
            location.text=json.getString("location")
            //TO REMOVE ALL EXISTING VALUES INSIDE SHAREDPREFERENCES
            /*val editor= sharedPref.edit()
            editor.clear()
            editor.apply()*/
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.show_profile_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            // Edit
            R.id.edit_menu_item -> {
                val intent = Intent(this, EditProfileActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}