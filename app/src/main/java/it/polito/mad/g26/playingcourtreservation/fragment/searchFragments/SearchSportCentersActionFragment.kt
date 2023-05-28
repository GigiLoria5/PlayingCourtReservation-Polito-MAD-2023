package it.polito.mad.g26.playingcourtreservation.fragment.searchFragments

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.adapter.searchCourtAdapters.CityResultAdapter
import it.polito.mad.g26.playingcourtreservation.util.hideActionBar
import it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments.SearchSportCentersActionVM

@AndroidEntryPoint
class SearchSportCentersActionFragment : Fragment(R.layout.search_cities_fragment) {

    private val args: SearchSportCentersActionFragmentArgs by navArgs()
    private val vm by viewModels<SearchSportCentersActionVM>()

    private lateinit var searchInputET: EditText
    private lateinit var citiesResultRV: RecyclerView

    /* ARGS */
    private var city: String = ""
    private var bornFrom: String = ""
    private var dateTime: Long = 0
    private var sportId: Int = 0
    private var selectedServicesIds: IntArray = intArrayOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        city = args.city
        bornFrom = args.bornFrom
        dateTime = args.dateTime
        sportId = args.sportId
        selectedServicesIds = args.selectedServicesIds


        /* CUSTOM TOOLBAR BACK MANAGEMENT*/
        val customToolBar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.customToolBar)
        customToolBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        customToolBar.title = "Find your Sport Center"
        searchInputET = view.findViewById(R.id.searchInputET)
        searchInputET.doOnTextChanged { text, _, _, _ ->
            vm.searchNameChanged(text.toString())
        }
        searchInputET.requestFocus()
        openKeyboard()
        /* CITIES RESULTS RECYCLE VIEW INITIALIZER*/
        citiesResultRV = view.findViewById(R.id.citiesResultRV)

        val cityResultAdapter = CityResultAdapter(vm.cities.value ?: listOf()) {
            //comingFrom: result - coming from da results page
            //comingFrom: home - coming from home page
            closeKeyboard()
            when (bornFrom) {
                "result" -> {
                    findNavController().popBackStack()
                    findNavController().popBackStack()
                }

                "home" -> {
                    findNavController().popBackStack()
                }
            }
            findNavController().navigate(
                HomePageFragmentDirections.actionHomeToSearchSportCenters(
                    "actionSearch",
                    it,
                    dateTime,
                    sportId,
                    selectedServicesIds
                )
            )
        }

        citiesResultRV.adapter = cityResultAdapter

        vm.cities.observe(viewLifecycleOwner) {
            cityResultAdapter.updateCollection(vm.cities.value ?: listOf())
        }
        searchInputET.setText(city)
    }

    private fun openKeyboard() {
        val inputMethodManager: InputMethodManager =
            requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(searchInputET, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun closeKeyboard() {
        val view: View? = this.view
        if (view != null) {
            val inputMethodManager =
                requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onResume() {
        super.onResume()
        hideActionBar(activity)
    }
}