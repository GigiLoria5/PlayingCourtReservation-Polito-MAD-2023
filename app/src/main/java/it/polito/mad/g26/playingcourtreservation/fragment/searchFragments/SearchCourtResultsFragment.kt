package it.polito.mad.g26.playingcourtreservation.fragment.searchFragments

import android.annotation.SuppressLint
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Button
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
    private lateinit var courtTypeMCV: MaterialCardView
    private lateinit var servicesRV: RecyclerView
    private lateinit var sportCentersRV: RecyclerView
    private lateinit var noCourtFoundMCV: MaterialCardView
    private lateinit var reservationMCV: MaterialCardView
    private lateinit var selectionTutorialTV: TextView

    /* LOGIC OBJECT OF THIS FRAGMENT */
    private val searchResultUtils = SearchCourtResultsUtil

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //TODO ELIMINARE NAVBAR

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
        courtTypeMCV = view.findViewById(R.id.courtTypeMCV)
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
            { vm.addServiceIdToFilters(it) },
            { vm.removeServiceIdFromFilters(it) },
            { vm.isServiceIdInList(it) }
        )
        servicesRV.adapter = servicesAdapter

        vm.services.observe(viewLifecycleOwner) {
            servicesAdapter.updateCollection(vm.services.value ?: listOf())
        }

        /*RESERVE A COURT TV */
        selectionTutorialTV = view.findViewById(R.id.selectionTutorialTV)

        /* SPORT CENTERS RECYCLE VIEW INITIALIZER*/
        sportCentersRV = view.findViewById(R.id.sportCentersRV)
        noCourtFoundMCV = view.findViewById(R.id.noCourtFoundMCV)
        val sportCentersAdapter = SportCenterAdapter(
            vm.getSportCentersWithDataFormatted(),
            { vm.courtReservationState(it) },
            { sportCenterId, serviceId -> vm.isServiceIdInSelectionList(sportCenterId, serviceId) }, //TODO AGGIUNGI FUNZIONE X MOSTRARE POPUP CON CONFERMA RESERVATION
            {sportCenterId, serviceId -> vm.addServiceSelectionToSportCenter(sportCenterId, serviceId )},
            {sportCenterId, serviceId -> vm.removeServiceSelectionFromSportCenter(sportCenterId, serviceId )},
        )

        vm.sportCentersCount.observe(viewLifecycleOwner) {
            if (it > 0) {
                if (vm.myReservation.value == null) sportCentersRV.visibility = View.VISIBLE
                noCourtFoundMCV.visibility = View.GONE

            } else {
                sportCentersRV.visibility = View.INVISIBLE
                noCourtFoundMCV.visibility = View.VISIBLE
            }
        }

        sportCentersRV.adapter = sportCentersAdapter
        vm.sportCenters.observe(viewLifecycleOwner) {
            vm.updateSelectedServicesPerSportCenter()
            sportCentersAdapter.updateCollection(vm.getSportCentersWithDataFormatted())
        }
        vm.services.observe(viewLifecycleOwner) {
            sportCentersAdapter.updateCollection(vm.getSportCentersWithDataFormatted())
        }

        /* RESERVATION OPTIONALITY INITIALIZER*/
        reservationMCV = view.findViewById(R.id.reservationMCV)
        vm.reservations.observe(viewLifecycleOwner) {
            sportCentersAdapter.reservationsUpdate()
        }

        vm.myReservation.observe(viewLifecycleOwner) { //X GESTIRE RESERVATIONS GIà PRESENTI
            if (it != null) {
                sportCentersRV.visibility = View.INVISIBLE
                servicesRV.visibility = View.GONE
                courtTypeACTV.visibility = View.INVISIBLE
                courtTypeMCV.visibility = View.INVISIBLE
                selectionTutorialTV.visibility = View.GONE
                reservationMCV.visibility = View.VISIBLE
                val reservationBTN = view.findViewById<Button>(R.id.reservationBTN)
                reservationBTN.setOnClickListener { /* NAVIGATION TO RESERVATION WITH "it" */ }
            } else {
                sportCentersRV.visibility = View.VISIBLE
                servicesRV.visibility = View.VISIBLE
                courtTypeACTV.visibility = View.VISIBLE
                courtTypeMCV.visibility = View.VISIBLE
                selectionTutorialTV.visibility = View.VISIBLE
                reservationMCV.visibility = View.GONE
            }
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