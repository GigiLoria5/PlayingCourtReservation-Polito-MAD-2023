package it.polito.mad.g26.playingcourtreservation.fragment.searchFragments

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.card.MaterialCardView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.activity.MainActivity

class SearchCourtFragment : Fragment(R.layout.fragment_search_court) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Update Title
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Playing Court Reservation"
        // Set Back Button
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        val cityNameTV = view.findViewById<TextView>(R.id.cityNameTV)
        val selectCityMCV = view.findViewById<MaterialCardView>(R.id.citySearchMCV)
        val searchMCV = view.findViewById<MaterialCardView>(R.id.searchMCV)
        cityNameTV.text = "Torino" //TODO TAKE USER CITY

        selectCityMCV.setOnClickListener {
            val direction =
                SearchCourtFragmentDirections.actionSearchCourtFragmentToSearchCourtActionFragment(
                    "home", cityNameTV.text.toString()
                )
            findNavController().navigate(direction)
        }
        searchMCV.setOnClickListener {
            val direction =
                SearchCourtFragmentDirections.actionSearchCourtFragmentToSearchCourtResultsFragment(
                    "home", cityNameTV.text.toString()
                )
            findNavController().navigate(direction)
        }
    }
}