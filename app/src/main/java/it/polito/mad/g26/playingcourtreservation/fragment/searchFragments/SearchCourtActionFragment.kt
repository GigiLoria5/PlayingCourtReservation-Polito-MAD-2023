package it.polito.mad.g26.playingcourtreservation.fragment.searchFragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.activity.MainActivity

class SearchCourtActionFragment : Fragment(R.layout.fragment_search_court_action) {

    private val args: SearchCourtActionFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Update Title TODO CHANGE
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Search Court action"
        // Set Back Button
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val tv = view.findViewById<TextView>(R.id.textView)
        tv.text = args.city

        val b = view.findViewById<Button>(R.id.searchButton)
        b.setOnClickListener {


            //comingFrom: actionSearch - arrivi da results page nato da action
            //comingFrom: home - arrivi dalla home page nato da home
            findNavController().navigate(
                when (args.bornFrom) {
                    "actionSearch" -> {
                        findNavController().popBackStack()
                        findNavController().popBackStack()
                        SearchCourtActionFragmentDirections.actionSearchCourtActionFragmentToSearchCourtResultsFragment(
                            "actionSearch", "Turin"
                        )
                    }
                    "home" -> {

                        SearchCourtActionFragmentDirections.actionSearchCourtActionFragmentToSearchCourtResultsFragment(
//                            SearchCourtFragmentDirections.actionSearchCourtFragmentToSearchCourtResultsFragment(

                            "actionSearch", "Turin"
                        )
                    }
                    else -> {
                        SearchCourtActionFragmentDirections.actionSearchCourtActionFragmentToSearchCourtResultsFragment(
                            "actionSearch",
                            "Turin"
                        )
                    }
                }
            )
        }
    }
}