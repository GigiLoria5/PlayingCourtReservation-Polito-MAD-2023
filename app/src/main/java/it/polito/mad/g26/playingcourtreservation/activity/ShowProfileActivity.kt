package it.polito.mad.g26.playingcourtreservation.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.TooltipCompat
import it.polito.mad.g26.playingcourtreservation.R

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