package it.polito.mad.g26.playingcourtreservation.fragment.searchFragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.card.MaterialCardView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.util.hideActionBar
import it.polito.mad.g26.playingcourtreservation.util.showActionBar
import org.json.JSONObject

class SearchSportCentersHomeFragment : Fragment(R.layout.fragment_search_sport_centers_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideActionBar(activity)

        val cityNameTV = view.findViewById<TextView>(R.id.cityNameTV)
        val selectCityMCV = view.findViewById<MaterialCardView>(R.id.citySearchMCV)
        val searchMCV = view.findViewById<MaterialCardView>(R.id.searchMCV)

        cityNameTV.text = getUserCity() ?: getString(R.string.default_city)
        selectCityMCV.setOnClickListener {
            val direction =
                SearchSportCentersHomeFragmentDirections.actionHomeToSportCentersAction(
                    "home", cityNameTV.text.toString()
                )
            findNavController().navigate(direction)
        }
        searchMCV.setOnClickListener {
            val direction =
                SearchSportCentersHomeFragmentDirections.actionHomeToSearchSportCenters(
                    "home", cityNameTV.text.toString()
                )
            findNavController().navigate(direction)
        }
    }

    private fun getUserCity(): String? {
        val sharedPref = this.requireActivity().getSharedPreferences("test", Context.MODE_PRIVATE)
        if (sharedPref.contains("profile")) {
            val json = sharedPref.getString("profile", "Default")?.let { JSONObject(it) }
            if (json != null) {
                return json.getString("location")
            }
            return null
        }
        return null
    }

    override fun onResume() {
        super.onResume()
        hideActionBar(activity)
    }

    override fun onPause() {
        super.onPause()
        showActionBar(activity)
    }
}