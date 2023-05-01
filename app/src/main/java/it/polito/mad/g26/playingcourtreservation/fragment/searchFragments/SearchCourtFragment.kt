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
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Search Court"
        // Set Back Button
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        val cityTV = view.findViewById<TextView>(R.id.cityTV)
        val selectCityMCV = view.findViewById<MaterialCardView>(R.id.selectCityMCV)
        val searchMCV = view.findViewById<MaterialCardView>(R.id.searchMCV)
        cityTV.text = "Turin" //TODO TAKE USER CITY

        selectCityMCV.setOnClickListener {
            val direction =
                SearchCourtFragmentDirections.actionSearchCourtFragmentToSearchCourtActionFragment(
                    "home", cityTV.text.toString()
                )
            findNavController().navigate(direction)
        }
        searchMCV.setOnClickListener {
            val direction =
                SearchCourtFragmentDirections.actionSearchCourtFragmentToSearchCourtResultsFragment(
                    "home", cityTV.text.toString()
                )
            findNavController().navigate(direction)
        }
    }
}