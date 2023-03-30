package it.polito.mad.g26.playingcourtreservation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

class EditProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // Set Back Button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}