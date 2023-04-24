package it.polito.mad.g26.playingcourtreservation.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.polito.mad.g26.playingcourtreservation.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = (
                supportFragmentManager
                    .findFragmentById(R.id.frame_layout) as NavHostFragment
                ).navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        // Handle Navigation between main fragments
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> navController.navigate(R.id.searchCourtFragment)
                R.id.reservations -> navController.navigate(R.id.reservationsFragment)
                R.id.profile -> navController.navigate(R.id.showProfileFragment)
            }
            true
        }
        // Handle Active Menu Item in the Navbar
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.searchCourtFragment -> bottomNav.menu.findItem(R.id.home).isChecked = true
                R.id.reservationsFragment -> bottomNav.menu.findItem(R.id.reservations).isChecked =
                    true
                R.id.showProfileFragment , R.id.editProfileFragment-> bottomNav.menu.findItem(R.id.profile).isChecked = true
            }
        }
    }

}