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
        cityTV.text = "Turin" //TODO TAKE USER CITY
        val cityMCV = view.findViewById<MaterialCardView>(R.id.cityMCV)
        cityMCV.setOnClickListener {
            val direction =
                SearchCourtFragmentDirections.actionSearchCourtFragmentToSearchCourtActionFragment(
                    cityTV.text.toString()
                )
            findNavController().navigate(direction)
        }
    }
}