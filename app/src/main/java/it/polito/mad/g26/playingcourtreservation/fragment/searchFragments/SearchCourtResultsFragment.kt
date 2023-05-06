package it.polito.mad.g26.playingcourtreservation.fragment.searchFragments

import android.annotation.SuppressLint
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.activity.MainActivity
import it.polito.mad.g26.playingcourtreservation.adapter.searchCourtAdapters.ServiceAdapter
import it.polito.mad.g26.playingcourtreservation.adapter.searchCourtAdapters.SportCenterAdapter
import it.polito.mad.g26.playingcourtreservation.util.SearchCourtResultsUtil
import it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments.SearchCourtResultsVM

class SearchCourtResultsFragment : Fragment(R.layout.fragment_search_court_results) {

    private val args: SearchCourtResultsFragmentArgs by navArgs()
    private val vm by viewModels<SearchCourtResultsVM>()

    /*   VISUAL COMPONENTS       */
    private lateinit var dateMCV: MaterialCardView
    private lateinit var dateTV: TextView
    private lateinit var hourMCV: MaterialCardView
    private lateinit var hourTV: TextView

    private lateinit var courtTypeACTV: AutoCompleteTextView
    private lateinit var servicesRV: RecyclerView
    private lateinit var sportCentersRV: RecyclerView

    /* LOGIC OBJECT OF THIS FRAGMENT */
    private val searchResultUtils = SearchCourtResultsUtil

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* VM INITIALIZATIONS */
        vm.setCity(args.city)

        //set search icon onclick
        val customSearchIconIV = view.findViewById<ImageView>(R.id.customSearchIconIV)
        customSearchIconIV.setOnClickListener {
            searchResultUtils.navigateToAction(findNavController(), args.city)
        }

        /* CUSTOM TOOLBAR BACK MANAGEMENT*/
        val customToolBar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.customToolBar)
        customToolBar.setNavigationOnClickListener {
            searchResultUtils.navigateBack(findNavController(), args.city, args.bornFrom)
        }

        /*BACK BUTTON MANAGEMENT*/
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            searchResultUtils.navigateBack(findNavController(), args.city, args.bornFrom)
        }

        //set onclick for title of custom toolbar
        customToolBar.setOnClickListener {
            searchResultUtils.navigateToAction(findNavController(), args.city)
        }

        //set title of custom toolbar
        customToolBar.title = "Courts in ${args.city}"

        /* COURT TYPE DROPDOWN MANAGEMENT*/
        courtTypeACTV = view.findViewById(R.id.courtTypeACTV)

        vm.sports.observe(viewLifecycleOwner) {
            searchResultUtils.setAutoCompleteTextViewSport(
                requireContext(),
                vm.sports.value,
                courtTypeACTV,
                vm.getSelectedSportId()
            )
        }
        courtTypeACTV.setOnItemClickListener { _, _, _, _ ->
            vm.selectedSportChanged(
                vm.sports.value?.find { it.name == courtTypeACTV.text.toString() }?.id ?: 0
            )
        }

        /* DATE MATERIAL CARD VIEW MANAGEMENT*/
        dateMCV = view.findViewById(R.id.dateMCV)
        dateTV = view.findViewById(R.id.dateTV)
        dateMCV.setOnClickListener {
            searchResultUtils.showDatePickerDialog(
                requireContext(),
                vm
            )
        }

        /* HOUR MATERIAL CARD VIEW MANAGEMENT*/
        hourMCV = view.findViewById(R.id.hourMCV)
        hourTV = view.findViewById(R.id.hourTV)
        hourMCV.setOnClickListener {
            searchResultUtils.showNumberPickerDialog(
                requireContext(),
                vm
            )
        }

        vm.selectedDateTimeMillis.observe(viewLifecycleOwner) {
            val c = Calendar.getInstance()
            c.timeInMillis = vm.selectedDateTimeMillis.value!!
            searchResultUtils.setDateTimeTextViews(
                c,
                getString(R.string.dateFormat),
                getString(R.string.hourFormat),
                dateTV,
                hourTV
            )
        }

        /* SERVICES RECYCLE VIEW INITIALIZER*/
        servicesRV = view.findViewById(R.id.servicesRV)
        val servicesAdapter = ServiceAdapter(
            vm.services.value ?: listOf(),
            { vm.addServiceId(it) },
            { vm.removeServiceId(it) },
            { vm.isServiceIdInList(it) }
        )
        servicesRV.adapter = servicesAdapter

        vm.services.observe(viewLifecycleOwner) {
            servicesAdapter.updateCollection(vm.services.value ?: listOf())
        }

        /* SPORT CENTERS RECYCLE VIEW INITIALIZER*/
        sportCentersRV = view.findViewById(R.id.sportCentersRV)
        val sportCentersAdapter = SportCenterAdapter(
            vm.sportCenters.value ?: listOf(),
            vm.services.value?: listOf(),
        ) { vm.courtReservationState(it) } //TODO AGGIUNGI FUNZIONE X VEDERE SUBITO SE HAI UNA PRENOTAZIONE X QUELLA DATA/ORA

        sportCentersRV.adapter = sportCentersAdapter

        vm.sportCenters.observe(viewLifecycleOwner) {
            sportCentersAdapter.updateCollection(vm.sportCenters.value ?: listOf())
        }
        vm.services.observe(viewLifecycleOwner){
            sportCentersAdapter.updateAvailableServices(vm.services.value?: listOf())
        }
        vm.reservations.observe(viewLifecycleOwner){
            sportCentersAdapter.reservationsUpdate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //show again original toolbar out of this fragment
        (activity as MainActivity).supportActionBar?.show()
    }

    @SuppressLint("RestrictedApi")
    override fun onResume() {
        super.onResume()
        // Remove Default Status Bar
        (activity as MainActivity).supportActionBar?.setShowHideAnimationEnabled(false)
        (activity as MainActivity).supportActionBar?.hide()

        //Restore autocomplete textview
        searchResultUtils.setAutoCompleteTextViewSport(
            requireContext(),
            vm.sports.value,
            courtTypeACTV,
            vm.getSelectedSportId()
        )
    }
}