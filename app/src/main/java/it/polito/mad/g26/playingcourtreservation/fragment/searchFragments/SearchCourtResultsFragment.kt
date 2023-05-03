package it.polito.mad.g26.playingcourtreservation.fragment.searchFragments

import android.annotation.SuppressLint
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ArrayAdapter
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
import it.polito.mad.g26.playingcourtreservation.util.SearchCourtResultsFragmentUtil
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

    /* LOGIC OBJECT OF THIS FRAGMENT */
    private val searchResultUtils = SearchCourtResultsFragmentUtil

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (vm.selectedDateTimeMillis.value == 0L) vm.changeSelectedDateTimeMillis(searchResultUtils.getMockInitialDateTime().timeInMillis)

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

        //set title of custom toolbar onclick
        customToolBar.setOnClickListener {
            searchResultUtils.navigateToAction(findNavController(), args.city)
        }

        //set title of custom toolbar
        customToolBar.title = "Courts in ${args.city}"

        /* POSITION DROPDOWN MANAGEMENT*/
        courtTypeACTV = view.findViewById(R.id.courtTypeACTV)
        courtTypeACTV.setText("Calcio a 11", false)
        val courtsType =
            listOf("Tutto", "Calcio a 5", "Calcio a 8", "Calcio a 11") //LI PRENDERAI DA LIVE DATA
        val adapterCourt = ArrayAdapter(view.context, R.layout.list_item, courtsType)
        courtTypeACTV.setAdapter(adapterCourt)

        /* DATE MATERIAL CARD VIEW MANAGEMENT*/
        dateMCV = view.findViewById(R.id.dateMCV)
        dateTV = view.findViewById(R.id.dateTV)
        dateMCV.setOnClickListener {
            searchResultUtils.showDatePickerDialog(
                view.context,
                vm
            )
        }

        /* HOUR MATERIAL CARD VIEW MANAGEMENT*/
        hourMCV = view.findViewById(R.id.hourMCV)
        hourTV = view.findViewById(R.id.hourTV)
        hourMCV.setOnClickListener {
            searchResultUtils.showNumberPickerDialog(
                view.context,
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

        /* SERVICES RECYCLE VIEW INITIALIZER*/ //TODO CAMBIA COLORE SERVICE SELEZIONATO
        servicesRV = view.findViewById(R.id.servicesRV)
        val servicesAdapter = ServiceAdapter(
            vm.services.value ?: listOf(),
            { vm.addService(it) },
            { vm.removeService(it) })
        servicesRV.adapter = servicesAdapter

        vm.services.observe(viewLifecycleOwner) {
            servicesAdapter.updateCollection(vm.services.value ?: listOf())
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
        val courtsType =
            listOf(
                "Tutto",
                "Calcio a 5",
                "Calcio a 8",
                "Calcio a 11"
            ) // TODO LI PRENDERAI DA LIVE DATA
        val adapterCourt = ArrayAdapter(requireContext(), R.layout.list_item, courtsType)
        courtTypeACTV.setAdapter(adapterCourt)
    }
}