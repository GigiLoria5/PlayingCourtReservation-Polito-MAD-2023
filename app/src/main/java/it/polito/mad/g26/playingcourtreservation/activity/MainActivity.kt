package it.polito.mad.g26.playingcourtreservation.activity

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LOCKED
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.util.isItemChecked
import it.polito.mad.g26.playingcourtreservation.util.makeGone
import it.polito.mad.g26.playingcourtreservation.util.makeVisible
import it.polito.mad.g26.playingcourtreservation.util.setCheckedMenuItem

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottomNavigationView)
        val navController = (supportFragmentManager.findFragmentById(R.id.frame_layout)
                as NavHostFragment).navController

        // Handle Navigation between main fragments
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> navigateToFragment(
                    navController,
                    R.id.home,
                    R.id.searchSportCentersHomeFragment
                )

                R.id.reservations -> navigateToFragment(
                    navController,
                    R.id.reservations,
                    R.id.reservationsFragment
                )

                R.id.profile -> navigateToFragment(
                    navController,
                    R.id.profile,
                    R.id.showProfileFragment
                )
            }
            true
        }

        // Handle Views Behaviors
        navController.addOnDestinationChangedListener { _, destination, _ ->
            Handler(Looper.getMainLooper()).post {
                when (destination.id) {
                    // Home
                    R.id.searchSportCentersHomeFragment -> {
                        requestedOrientation = SCREEN_ORIENTATION_LOCKED
                        bottomNav.setCheckedMenuItem(R.id.home)
                    }

                    R.id.searchSportCentersActionFragment -> lockOrientationAndHideNav()

                    R.id.searchSportCentersFragment -> lockOrientationAndShowNav()

                    R.id.searchCourtsFragment -> lockOrientationAndHideNav()

                    R.id.courtReviewsFragment -> lockOrientationAndShowNav()

                    // Reservations
                    R.id.reservationsFragment -> {
                        requestedOrientation = SCREEN_ORIENTATION_LOCKED
                        bottomNav.setCheckedMenuItem(R.id.reservations)
                    }

                    R.id.reservationDetailsFragment -> lockOrientationAndShowNav()

                    R.id.modifyReservationDetailsFragment -> lockOrientationAndHideNav()

                    // Profile
                    R.id.showProfileFragment -> {
                        requestedOrientation = SCREEN_ORIENTATION_UNSPECIFIED
                        bottomNav.setCheckedMenuItem(R.id.profile)
                    }

                    R.id.editProfileFragment -> setOrientationAndVisibility(
                        SCREEN_ORIENTATION_UNSPECIFIED,
                        false
                    )
                }
            }
        }

        // android navigation bar color (! not app navigation bar !)
        getColor(R.color.grey_light).also { window.navigationBarColor = it }
    }

    private fun lockOrientationAndShowNav() {
        setOrientationAndVisibility(SCREEN_ORIENTATION_LOCKED, true)
    }

    private fun lockOrientationAndHideNav() {
        setOrientationAndVisibility(SCREEN_ORIENTATION_LOCKED, false)
    }

    // This method is thought to be called by subViews
    private fun setOrientationAndVisibility(orientation: Int, isBottomNavVisible: Boolean) {
        requestedOrientation = orientation
        if (isBottomNavVisible) {
            bottomNav.makeVisible()
            uncheckAllMenuItems()
            return
        }
        bottomNav.makeGone()
    }

    private fun navigateToFragment(navController: NavController, itemId: Int, destinationId: Int) {
        if (bottomNav.isItemChecked(itemId)) {
            // If the current destination is already selected, do nothing
            return
        }
        navController.navigate(destinationId)
    }

    private fun uncheckAllMenuItems() {
        bottomNav.menu.setGroupCheckable(0, true, false)
        for (i in 0 until bottomNav.menu.size()) {
            bottomNav.menu.getItem(i).isChecked = false
        }
        bottomNav.menu.setGroupCheckable(0, true, true)
    }

}