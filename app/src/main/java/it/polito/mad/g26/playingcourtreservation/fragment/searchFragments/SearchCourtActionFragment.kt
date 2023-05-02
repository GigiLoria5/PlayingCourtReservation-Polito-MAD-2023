package it.polito.mad.g26.playingcourtreservation.fragment.searchFragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.activity.MainActivity
import it.polito.mad.g26.playingcourtreservation.adapter.searchCourtAdapters.CityResultAdapter
import it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments.SearchCourtActionVM

class SearchCourtActionFragment : Fragment(R.layout.fragment_search_court_action) {

    private val args: SearchCourtActionFragmentArgs by navArgs()
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
        customToolBar.title = "Find your court"
        searchInputET = view.findViewById(R.id.searchInputET)
        searchInputET.requestFocus()
        val inputMethodManager: InputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(searchInputET, InputMethodManager.SHOW_IMPLICIT);

        searchInputET.doOnTextChanged { text, _, _, _ ->
            vm.searchNameChanged(text.toString())
        }

        /* CITIES RESULTS RECYCLE VIEW INITIALIZER*/
        citiesResultRV = view.findViewById(R.id.citiesResultRV)


        val cityResultAdapter = CityResultAdapter(vm.cities.value ?: listOf<String>()) {
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
                else -> {}
            }
            findNavController().navigate(
                SearchCourtFragmentDirections.actionSearchCourtFragmentToSearchCourtResultsFragment(
                    "actionSearch",
                    it
                )
            )
        }

        citiesResultRV.adapter = cityResultAdapter

        vm.cities.observe(viewLifecycleOwner) {
            //AGGIORNA RECYCLE VIEW
            cityResultAdapter.updateCollection(vm.cities.value ?: listOf<String>())
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