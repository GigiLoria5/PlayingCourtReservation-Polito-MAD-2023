package it.polito.mad.g26.playingcourtreservation.fragment.searchFragments

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.EditText
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
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Search Your Court"
        // Set Back Button
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val searchInputET = view.findViewById<EditText>(R.id.searchInputET)
        searchInputET.hint = "arg: ${args.city}"
        searchInputET.requestFocus()
        val imgr: InputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imgr.showSoftInput(searchInputET, InputMethodManager.SHOW_IMPLICIT);

        //questa è la funzione per andare in search. Al posto di "Turin" ci sarà la città scelta
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