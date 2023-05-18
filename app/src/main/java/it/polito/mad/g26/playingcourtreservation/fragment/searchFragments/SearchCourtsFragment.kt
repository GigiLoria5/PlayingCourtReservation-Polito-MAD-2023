package it.polito.mad.g26.playingcourtreservation.fragment.searchFragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.util.setupActionBar
import it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments.SearchCourtsVM

class SearchCourtsFragment : Fragment(R.layout.fragment_search_courts) {

    private val args: SearchCourtsFragmentArgs by navArgs()
    private val vm by viewModels<SearchCourtsVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBar(activity, "Search Courts", true)
        val sportCenterId = args.sportCenterId
        vm.setSportCenterId(sportCenterId)

        // TODO: remove this click listener later
        val toBeRemovedSoon = view.findViewById<TextView>(R.id.searchCourtsLabelToBeRemoved)
        toBeRemovedSoon.setOnClickListener {
            val direction =
                SearchCourtsFragmentDirections.actionSearchCourtsToCourtReviews(1)
            findNavController().navigate(direction)
        }


        //init rv services
        //init rv courts

        vm.sportCenter.observe(viewLifecycleOwner) {
            val sportCenterWithDetailsFormatted = vm.getSportCenterWithDetailsFormatted()
            val sportCenter = sportCenterWithDetailsFormatted.sportCenter
            val reviewsSummary = sportCenterWithDetailsFormatted.sportCenterReviewsSummary
            val courts = sportCenterWithDetailsFormatted.courts
            val servicesWithFee = sportCenterWithDetailsFormatted.servicesWithFee

            toBeRemovedSoon.text =sportCenter.name + " "+toBeRemovedSoon.text


            //update sport center texts
            //update services rv
            //update courts rv (courtsAdapter.updateCollection(courts))
        }

        vm.reviews.observe(viewLifecycleOwner){
            toBeRemovedSoon.text=toBeRemovedSoon.text.toString()+it.avg
        }


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