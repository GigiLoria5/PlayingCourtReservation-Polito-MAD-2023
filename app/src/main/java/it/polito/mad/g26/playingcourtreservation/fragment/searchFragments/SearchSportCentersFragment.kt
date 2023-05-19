package it.polito.mad.g26.playingcourtreservation.fragment.searchFragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.card.MaterialCardView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.adapter.searchCourtAdapters.ServiceAdapter
import it.polito.mad.g26.playingcourtreservation.adapter.searchCourtAdapters.SportCenterAdapter
import it.polito.mad.g26.playingcourtreservation.util.HorizontalSpaceItemDecoration
import it.polito.mad.g26.playingcourtreservation.util.SearchSportCentersUtil
import it.polito.mad.g26.playingcourtreservation.util.hideActionBar
import it.polito.mad.g26.playingcourtreservation.util.makeGone
import it.polito.mad.g26.playingcourtreservation.util.makeInvisible
import it.polito.mad.g26.playingcourtreservation.util.makeVisible
import it.polito.mad.g26.playingcourtreservation.util.showActionBar
import it.polito.mad.g26.playingcourtreservation.util.startShimmerAnimation
import it.polito.mad.g26.playingcourtreservation.util.stopShimmerAnimation
import it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments.SearchSportCentersVM

class SearchSportCentersFragment : Fragment(R.layout.fragment_search_sport_centers) {

    private val args: SearchSportCentersFragmentArgs by navArgs()
    private val vm by viewModels<SearchSportCentersVM>()

    /*   VISUAL COMPONENTS       */
    private lateinit var dateMCV: MaterialCardView
    private lateinit var dateTV: TextView
    private lateinit var hourMCV: MaterialCardView
    private lateinit var hourTV: TextView
    private lateinit var servicesShimmerView: ShimmerFrameLayout

    private lateinit var courtTypeACTV: AutoCompleteTextView
    private lateinit var courtTypeMCV: MaterialCardView
    private lateinit var servicesRV: RecyclerView
    private lateinit var sportCentersRV: RecyclerView
    private lateinit var sportCentersShimmerView: ShimmerFrameLayout
    private lateinit var noSportCentersFoundTV: TextView
    private lateinit var existingReservationCL: ConstraintLayout
    private lateinit var numberOfSportCentersFoundTV: TextView

    /* LOGIC OBJECT OF THIS FRAGMENT */
    private val searchSportCentersUtil = SearchSportCentersUtil

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val city = args.city
        val bornFrom = args.bornFrom

        /* VM INITIALIZATIONS */
        vm.setCity(city)

        /* CUSTOM TOOLBAR MANAGEMENT*/
        val customToolBar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.customToolBar)
        customToolBar.setNavigationOnClickListener {
            searchSportCentersUtil.navigateBack(findNavController(), city, bornFrom)
        }

        val customSearchIconIV = view.findViewById<ImageView>(R.id.customSearchIconIV)
        customSearchIconIV.setOnClickListener {
            searchSportCentersUtil.navigateToAction(findNavController(), city)
        }

        customToolBar.setOnClickListener {
            searchSportCentersUtil.navigateToAction(findNavController(), city)
        }

        customToolBar.title = "Sport Centers in $city"

        /*BACK BUTTON MANAGEMENT*/
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            searchSportCentersUtil.navigateBack(findNavController(), city, bornFrom)
        }

        /* COURT TYPE DROPDOWN MANAGEMENT*/
        courtTypeACTV = view.findViewById(R.id.courtTypeACTV)
        courtTypeMCV = view.findViewById(R.id.courtTypeMCV)
        vm.sports.observe(viewLifecycleOwner) {
            searchSportCentersUtil.setAutoCompleteTextViewSport(
                requireContext(),
                vm.sports.value,
                courtTypeACTV,
                vm.getSelectedSportId()
            )
        }
        courtTypeACTV.setOnItemClickListener { _, _, _, _ ->
            vm.changeSelectedSport(
                vm.sports.value?.find { it.name == courtTypeACTV.text.toString() }?.id ?: 0
            )
        }

        /* DATE MATERIAL CARD VIEW MANAGEMENT*/
        dateMCV = view.findViewById(R.id.dateMCV)
        dateTV = view.findViewById(R.id.dateTV)
        dateMCV.setOnClickListener {
            searchSportCentersUtil.showAndManageBehaviorDatePickerDialog(
                requireContext(),
                vm.selectedDateTimeMillis.value!!
            ) { vm.changeSelectedDateTimeMillis(it) }
        }

        /* HOUR MATERIAL CARD VIEW MANAGEMENT*/
        hourMCV = view.findViewById(R.id.hourMCV)
        hourTV = view.findViewById(R.id.hourTV)
        hourMCV.setOnClickListener {
            searchSportCentersUtil.showAndManageBehaviorTimePickerDialog(
                requireContext(),
                vm.selectedDateTimeMillis.value!!,
            ) { vm.changeSelectedDateTimeMillis(it) }
        }

        vm.selectedDateTimeMillis.observe(viewLifecycleOwner) {
            searchSportCentersUtil.setDateTimeTextViews(
                vm.selectedDateTimeMillis.value ?: 0,
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
        val itemDecoration =
            HorizontalSpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.chipDistance))
        servicesRV.addItemDecoration(itemDecoration)

        /* servicesShimmerView INITIALIZER */
        servicesShimmerView = view.findViewById(R.id.servicesShimmerView)
        vm.services.observe(viewLifecycleOwner) {
            servicesShimmerView.startShimmerAnimation(servicesRV)
            Handler(Looper.getMainLooper()).postDelayed({
                servicesShimmerView.stopShimmerAnimation(servicesRV)
                servicesAdapter.updateCollection(vm.services.value ?: listOf())
            }, 200)
        }

        /*NUMBER OF SPORT CENTERS FOUND TV */
        numberOfSportCentersFoundTV = view.findViewById(R.id.numberOfSportCentersFoundTV)
        numberOfSportCentersFoundTV.makeGone()

        /* SPORT CENTERS RECYCLE VIEW INITIALIZER*/
        sportCentersRV = view.findViewById(R.id.sportCentersRV)
        noSportCentersFoundTV = view.findViewById(R.id.noSportCentersFoundTV)
        val sportCentersAdapter = SportCenterAdapter(
            vm.getSportCentersWithDetailsFormatted(),
            { serviceId ->
                vm.isServiceIdInList(serviceId)
            },
            {
                val direction =
                    SearchSportCentersFragmentDirections.actionSearchSportCentersToSearchCourts(it)
                findNavController().navigate(direction)

            }
        )
        sportCentersRV.adapter = sportCentersAdapter

        /* shimmerFrameLayout INITIALIZER */
        sportCentersShimmerView = view.findViewById(R.id.sportCentersShimmerView)
        vm.sportCentersMediator.observe(viewLifecycleOwner) {
            // Start Shimmer loading
            sportCentersShimmerView.startShimmerAnimation(sportCentersRV)
            numberOfSportCentersFoundTV.makeGone()
            noSportCentersFoundTV.makeGone()
            existingReservationCL.makeGone()
            // Stop Shimmer loading
            Handler(Looper.getMainLooper()).postDelayed({
                sportCentersShimmerView.stopShimmerAnimation(sportCentersRV)
                if (vm.existingReservationIdByDateAndTime.value == null) {
                    numberOfSportCentersFoundTV.makeVisible()
                    val sportCentersWithDetailsFormatted =
                        vm.getSportCentersWithDetailsFormatted()
                    val numberOfSportCentersFound = sportCentersWithDetailsFormatted.size
                    numberOfSportCentersFoundTV.text = view.context.getString(
                        R.string.searchSportCenterResultsInfo,
                        numberOfSportCentersFound,
                        if (numberOfSportCentersFound != 1) "s" else ""
                    )
                    if (numberOfSportCentersFound > 0) {
                        noSportCentersFoundTV.makeGone()
                        sportCentersRV.makeVisible()
                        sportCentersAdapter.updateCollection(sportCentersWithDetailsFormatted)
                    } else {
                        sportCentersRV.makeInvisible()
                        noSportCentersFoundTV.makeVisible()
                    }
                } else {
                    numberOfSportCentersFoundTV.makeGone()
                    sportCentersRV.makeInvisible()
                    existingReservationCL.makeVisible()
                }
            }, 200)
        }

        /* EXISTING RESERVATION INITIALIZER*/
        existingReservationCL = view.findViewById(R.id.existingReservationCL)
        vm.existingReservationIdByDateAndTime.observe(viewLifecycleOwner) {
            Handler(Looper.getMainLooper()).postDelayed({
                if (it != null) {
                    sportCentersRV.makeInvisible()
                    servicesRV.makeGone()
                    courtTypeACTV.makeInvisible()
                    courtTypeMCV.makeInvisible()
                    numberOfSportCentersFoundTV.makeGone()
                    existingReservationCL.makeVisible()
                    noSportCentersFoundTV.makeGone()

                    val navigateToReservationBTN =
                        view.findViewById<Button>(R.id.navigateToReservationBTN)
                    navigateToReservationBTN.setOnClickListener { _ ->
                        findNavController().navigate(
                            SearchSportCentersFragmentDirections.actionSearchSportCentersToReservationDetails(
                                it
                            )
                        )
                    }
                } else {
                    if (vm.getSportCentersWithDetailsFormatted().isNotEmpty())
                        sportCentersRV.makeVisible()
                    servicesRV.makeVisible()
                    courtTypeACTV.makeVisible()
                    courtTypeMCV.makeVisible()
                    numberOfSportCentersFoundTV.makeVisible()
                    existingReservationCL.makeGone()
                }
            }, 300)
        }
    }

    override fun onResume() {
        super.onResume()
        hideActionBar(activity)
        //Restore autocomplete textview
        searchSportCentersUtil.setAutoCompleteTextViewSport(
            requireContext(),
            vm.sports.value,
            courtTypeACTV,
            vm.getSelectedSportId()
        )
    }

    override fun onPause() {
        super.onPause()
        showActionBar(activity)
    }
}