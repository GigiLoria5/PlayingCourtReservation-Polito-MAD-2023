package it.polito.mad.g26.playingcourtreservation.fragment.searchFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.activity.MainActivity


class SearchCourtResultsFragment : Fragment(R.layout.fragment_search_court_results) {

    private val args: SearchCourtResultsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Update Title TODO CHANGE
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Search Court results"
        // Set Back Button
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val tv = view.findViewById<TextView>(R.id.textView)
        tv.text = "Selected city: ${args.city}"

        val b = view.findViewById<Button>(R.id.searchButton)
        b.setOnClickListener {
            val direction =
                SearchCourtResultsFragmentDirections.actionSearchCourtResultsFragmentToSearchCourtActionFragment(
                    "Turin"
                )
            findNavController().navigate(direction)
        }

    }
}