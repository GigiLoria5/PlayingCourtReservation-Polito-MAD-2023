package it.polito.mad.g26.playingcourtreservation.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
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
                R.id.showProfileFragment, R.id.editProfileFragment -> bottomNav.menu.findItem(R.id.profile).isChecked =
                    true
            }
        }
        // Set Back Button Function
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                // Handle the menu selection
                return when (item.itemId) {
                    // Back
                    android.R.id.home -> {
                        navController.popBackStack()
                        true
                    }
                    else -> false
                }
            }
        }, this, Lifecycle.State.RESUMED)

            window.navigationBarColor = getColor(R.color.grey_light);


    }

}