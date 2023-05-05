package it.polito.mad.g26.playingcourtreservation.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.polito.mad.g26.playingcourtreservation.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navController = (supportFragmentManager.findFragmentById(R.id.frame_layout)
                as NavHostFragment).navController

        // Handle Navigation between main fragments
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> navigateToFragment(
                    bottomNav,
                    navController,
                    R.id.home,
                    R.id.searchCourtFragment
                )

                R.id.reservations -> navigateToFragment(
                    bottomNav,
                    navController,
                    R.id.reservations,
                    R.id.reservationsFragment
                )

                R.id.profile -> navigateToFragment(
                    bottomNav,
                    navController,
                    R.id.profile,
                    R.id.showProfileFragment
                )
            }
            true
        }

        // Handle Active Menu Item in the Navbar
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.searchCourtFragment ->
                    bottomNav.menu.findItem(R.id.home).isChecked = true

                R.id.reservationsFragment ->
                    bottomNav.menu.findItem(R.id.reservations).isChecked = true

                R.id.showProfileFragment, R.id.editProfileFragment ->
                    bottomNav.menu.findItem(R.id.profile).isChecked = true
            }
        }
    }

    private fun navigateToFragment(
        bottomNav: BottomNavigationView,
        navController: NavController,
        itemId: Int,
        destinationId: Int
    ) {
        if (bottomNav.menu.findItem(itemId).isChecked) {
            // If the current destination is already selected, do nothing
            return
        }
        navController.navigate(destinationId)
    }

}