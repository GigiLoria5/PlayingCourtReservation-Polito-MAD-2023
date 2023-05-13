package it.polito.mad.g26.playingcourtreservation.fragment.searchFragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.activity.MainActivity
import it.polito.mad.g26.playingcourtreservation.adapter.searchCourtAdapters.CityResultAdapter
import it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments.SearchCourtActionVM

class SearchSportCentersActionFragment : Fragment(R.layout.fragment_search_sport_centers_action) {

    private val args: SearchSportCentersActionFragmentArgs by navArgs()
    private val vm by viewModels<SearchCourtActionVM>()

    private lateinit var searchInputET: EditText
    private lateinit var citiesResultRV: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* CUSTOM TOOLBAR BACK MANAGEMENT*/
        val customToolBar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.customToolBar)
        customToolBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        customToolBar.title = "Find your Sport Center"
        searchInputET = view.findViewById(R.id.searchInputET)
        searchInputET.requestFocus()
        val inputMethodManager: InputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(searchInputET, InputMethodManager.SHOW_IMPLICIT)
        searchInputET.doOnTextChanged { text, _, _, _ ->
            vm.searchNameChanged(text.toString())
        }

        /* CITIES RESULTS RECYCLE VIEW INITIALIZER*/
        citiesResultRV = view.findViewById(R.id.citiesResultRV)

        val cityResultAdapter = CityResultAdapter(vm.cities.value ?: listOf()) {
            //comingFrom: result - arrivi da results page
            //comingFrom: home - arrivi dalla home page

            when (args.bornFrom) {
                "result" -> {
                    findNavController().popBackStack()
                    findNavController().popBackStack()
                }

                "home" -> {
                    findNavController().popBackStack()
                }
            }
            findNavController().navigate(
                SearchSportCentersHomeFragmentDirections.actionHomeToSearchSportCenters(
                    "actionSearch",
                    it
                )
            )
        }

        citiesResultRV.adapter = cityResultAdapter

        vm.cities.observe(viewLifecycleOwner) {
            //AGGIORNA RECYCLE VIEW
            cityResultAdapter.updateCollection(vm.cities.value ?: listOf())
        }
        searchInputET.setText(args.city)
    }

    @SuppressLint("RestrictedApi")
    override fun onResume() {
        super.onResume()
        // Remove Default Status Bar
        (activity as MainActivity).supportActionBar?.setShowHideAnimationEnabled(false)
        (activity as MainActivity).supportActionBar?.hide()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //show again original toolbar out of this fragment
        (activity as MainActivity).supportActionBar?.show()
    }
}