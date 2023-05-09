package it.polito.mad.g26.playingcourtreservation.activity

import android.content.pm.ActivityInfo
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
                    R.id.searchCourtFragment
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
                    // Main Views: the bottom navigation must be visible
                    R.id.searchCourtFragment -> {
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
                        bottomNav.setCheckedMenuItem(R.id.home)
                    }

                    R.id.reservationsFragment -> {
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
                        bottomNav.setCheckedMenuItem(R.id.reservations)
                    }

                    R.id.showProfileFragment -> {
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                        bottomNav.setCheckedMenuItem(R.id.profile)
                    }
                    // Sub Views: the bottom navigation should not be visible
                    R.id.searchCourtActionFragment -> {
                        lockOrientationAndBottomNavMakeGone()
                    }

                    R.id.searchCourtResultsFragment -> {
                        lockOrientationAndBottomNavMakeGone()
                    }

                    R.id.reservationDetailsFragment -> {
                        lockOrientationAndBottomNavMakeGone()
                    }

                    R.id.editProfileFragment -> {
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                        bottomNav.makeGone()
                    }
                }
            }
        }

        // android navigation bar color (! not app navigation bar !)
        getColor(R.color.grey_light).also { window.navigationBarColor = it }
    }

    private fun lockOrientationAndBottomNavMakeGone() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
        bottomNav.makeGone()
    }

    private fun navigateToFragment(navController: NavController, itemId: Int, destinationId: Int) {
        if (bottomNav.isItemChecked(itemId)) {
            // If the current destination is already selected, do nothing
            return
        }
        navController.navigate(destinationId)
    }

}