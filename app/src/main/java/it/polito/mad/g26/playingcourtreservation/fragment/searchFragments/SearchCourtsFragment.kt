package it.polito.mad.g26.playingcourtreservation.fragment.searchFragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.util.setupActionBar

class SearchCourtsFragment : Fragment(R.layout.fragment_search_courts) {

    private val args: SearchCourtsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBar(activity, "Search Courts", true)

        val sportCenterId = args.sportCenterId
        println("sportCenterId = $sportCenterId")

        // Handle Menu Items
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_courts, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    // Back
                    android.R.id.home -> {
                        findNavController().popBackStack()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

}