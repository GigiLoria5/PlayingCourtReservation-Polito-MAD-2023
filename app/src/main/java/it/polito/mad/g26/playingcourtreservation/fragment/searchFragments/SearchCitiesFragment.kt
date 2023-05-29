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
import it.polito.mad.g26.playingcourtreservation.util.Debouncer
import it.polito.mad.g26.playingcourtreservation.util.UiState
import it.polito.mad.g26.playingcourtreservation.util.hideActionBar
import it.polito.mad.g26.playingcourtreservation.util.toast
import it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments.SearchCitiesViewModel

@AndroidEntryPoint
class SearchCitiesFragment : Fragment(R.layout.search_cities_fragment) {

    private val args: SearchCitiesFragmentArgs by navArgs()
    private val viewModel by viewModels<SearchCitiesViewModel>()

    private lateinit var searchInputET: EditText
    private lateinit var citiesResultRV: RecyclerView

    /* ARGS */
    private var city: String = ""
    private var bornFrom: String = ""
    private var dateTime: Long = 0
    private var sportId: String = ""
    private var selectedServicesIds: Array<String> = arrayOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        city = args.city
        bornFrom = args.bornFrom
        dateTime = args.dateTime
        sportId = args.sportName
        selectedServicesIds = args.selectedServicesNames

        /* CUSTOM TOOLBAR BACK MANAGEMENT*/
        val customToolBar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.customToolBar)
        customToolBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        customToolBar.title = "Find your Sport Center"
        searchInputET = view.findViewById(R.id.searchInputET)
        val searchDebouncer = Debouncer(500)
        searchInputET.doOnTextChanged { text, _, _, _ ->
            searchDebouncer.submit {
                viewModel.getCities(text.toString())
            }
        }
        searchInputET.requestFocus()
        openKeyboard()
        searchInputET.setText(city)

        /* CITIES RESULTS RECYCLE VIEW INITIALIZER*/
        citiesResultRV = view.findViewById(R.id.citiesResultRV)
        val cityResultAdapter = CityResultAdapter {
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

        // Get Cities
        viewModel.cities.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    // TODO: Start Shimmer Loading
                    println("SearchCitiesFragment Loading")
                }

                is UiState.Failure -> {
                    // TODO: Stop Shimmer Loading
                    toast(state.error ?: "Unable to get cities")
                }

                is UiState.Success -> {
                    // TODO: Stop Shimmer Loading
                    cityResultAdapter.updateCollection(state.result)
                }
            }
        }

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
