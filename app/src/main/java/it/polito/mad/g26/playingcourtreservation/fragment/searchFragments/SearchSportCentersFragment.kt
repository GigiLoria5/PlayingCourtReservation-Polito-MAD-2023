package it.polito.mad.g26.playingcourtreservation.fragment.searchFragments

import android.animation.LayoutTransition
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
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
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.adapter.searchCourtAdapters.ServiceAdapter
import it.polito.mad.g26.playingcourtreservation.adapter.searchCourtAdapters.SportCenterAdapter
import it.polito.mad.g26.playingcourtreservation.util.HorizontalSpaceItemDecoration
import it.polito.mad.g26.playingcourtreservation.util.SearchSportCentersUtils
import it.polito.mad.g26.playingcourtreservation.util.UiState
import it.polito.mad.g26.playingcourtreservation.util.hideActionBar
import it.polito.mad.g26.playingcourtreservation.util.makeGone
import it.polito.mad.g26.playingcourtreservation.util.makeInvisible
import it.polito.mad.g26.playingcourtreservation.util.makeVisible
import it.polito.mad.g26.playingcourtreservation.util.startShimmerAnimation
import it.polito.mad.g26.playingcourtreservation.util.stopShimmerAnimation
import it.polito.mad.g26.playingcourtreservation.util.toast
import it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments.SearchSportCentersViewModel

@AndroidEntryPoint
class SearchSportCentersFragment : Fragment(R.layout.search_sport_centers_fragment) {

    private val args: SearchSportCentersFragmentArgs by navArgs()
    private val viewModel by viewModels<SearchSportCentersViewModel>()

    /*   VISUAL COMPONENTS       */
    private lateinit var dateMCV: MaterialCardView
    private lateinit var dateTV: TextView
    private lateinit var hourMCV: MaterialCardView
    private lateinit var hourTV: TextView
    private lateinit var servicesShimmerView: ShimmerFrameLayout

    private lateinit var courtTypeACTV: AutoCompleteTextView
    private lateinit var courtTypeMCV: MaterialCardView
    private lateinit var selectedSportShimmerView: ShimmerFrameLayout

    private lateinit var servicesRV: RecyclerView
    private lateinit var sportCentersRV: RecyclerView
    private lateinit var sportCentersShimmerView: ShimmerFrameLayout
    private lateinit var noSportCentersFoundTV: TextView
    private lateinit var existingReservationCL: ConstraintLayout
    private lateinit var numberOfSportCentersFoundCL: ConstraintLayout
    private lateinit var numberOfSportCentersFoundTV: TextView

    private lateinit var sportCentersAdapter: SportCenterAdapter
    private lateinit var servicesAdapter: ServiceAdapter
    private lateinit var navigateToReservationBTN: Button

    /* LOGIC OBJECT OF THIS FRAGMENT */
    private val searchSportCentersUtil = SearchSportCentersUtils

    /* ARGS */
    private var city: String = ""
    private var bornFrom: String = ""
    private var dateTime: Long = 0
    private var sportName: String = ""
    private var selectedServicesNames: Array<String> = arrayOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        city = args.city
        bornFrom = args.bornFrom
        dateTime = args.dateTime
        sportName = args.sportName
        selectedServicesNames = args.selectedServicesNames

        /* VM INITIALIZATIONS */
        viewModel.initialize(city, dateTime, sportName, selectedServicesNames)

        /* CUSTOM TOOLBAR MANAGEMENT*/
        val customToolBar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.customToolBar)
        customToolBar.setNavigationOnClickListener {
            searchSportCentersUtil.navigateBack(findNavController(), city, bornFrom)
        }

        val customSearchIconIV = view.findViewById<ImageView>(R.id.customSearchIconIV)
        customSearchIconIV.setOnClickListener {
            searchSportCentersUtil.navigateToAction(
                findNavController(),
                city,
                viewModel.selectedDateTimeMillis.value!!,
                viewModel.getSelectedSportName(),
                viewModel.getSelectedServices()
            )
        }

        customToolBar.setOnClickListener {
            searchSportCentersUtil.navigateToAction(
                findNavController(),
                city,
                viewModel.selectedDateTimeMillis.value!!,
                viewModel.getSelectedSportName(),
                viewModel.getSelectedServices()
            )
        }

        customToolBar.title = "Sport Centers"
        customToolBar.subtitle = city

        /*BACK BUTTON MANAGEMENT*/
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            searchSportCentersUtil.navigateBack(findNavController(), city, bornFrom)
        }

        /* COURT TYPE DROPDOWN MANAGEMENT*/
        courtTypeACTV = view.findViewById(R.id.courtTypeACTV)
        courtTypeMCV = view.findViewById(R.id.courtTypeMCV)
        courtTypeACTV.setOnItemClickListener { _, _, _, _ ->
            viewModel.changeSelectedSport(courtTypeACTV.text.toString())
        }

        /* selectedSportShimmerView INITIALIZER */
        selectedSportShimmerView = view.findViewById(R.id.selectedSportShimmerView)


        /* DATE MATERIAL CARD VIEW MANAGEMENT*/
        dateMCV = view.findViewById(R.id.dateMCV)
        dateTV = view.findViewById(R.id.dateTV)
        dateMCV.setOnClickListener {
            searchSportCentersUtil.showAndManageBehaviorDatePickerDialog(
                requireContext(),
                viewModel.selectedDateTimeMillis.value!!
            ) { viewModel.changeSelectedDateTimeMillis(it) }
        }

        /* HOUR MATERIAL CARD VIEW MANAGEMENT*/
        hourMCV = view.findViewById(R.id.hourMCV)
        hourTV = view.findViewById(R.id.hourTV)
        hourMCV.setOnClickListener {
            searchSportCentersUtil.showAndManageBehaviorTimePickerDialog(
                requireContext(),
                viewModel.selectedDateTimeMillis.value!!,
            ) { viewModel.changeSelectedDateTimeMillis(it) }
        }

        viewModel.selectedDateTimeMillis.observe(viewLifecycleOwner) {
            searchSportCentersUtil.setDateTimeTextViews(
                viewModel.selectedDateTimeMillis.value ?: 0,
                getString(R.string.date_format),
                getString(R.string.hour_format),
                dateTV,
                hourTV
            )
            loadExistingReservation() // When the date/time changes, it is necessary to check again whether a reservation exists
        }

        /* SERVICES RECYCLE VIEW INITIALIZER*/
        servicesRV = view.findViewById(R.id.servicesRV)
        servicesAdapter = createServiceAdapter()
        servicesRV.adapter = servicesAdapter
        val itemDecoration =
            HorizontalSpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.chip_distance))
        servicesRV.addItemDecoration(itemDecoration)

        /* servicesShimmerView INITIALIZER */
        servicesShimmerView = view.findViewById(R.id.servicesShimmerView)

        /*NUMBER OF SPORT CENTERS FOUND RV AND TV */
        numberOfSportCentersFoundCL = view.findViewById(R.id.numberOfSportCentersFoundCL)
        numberOfSportCentersFoundTV = view.findViewById(R.id.numberOfSportCentersFoundTV)

        /* SPORT CENTERS RECYCLE VIEW INITIALIZER*/
        sportCentersRV = view.findViewById(R.id.sportCentersRV)
        noSportCentersFoundTV = view.findViewById(R.id.noSportCentersFoundTV)
        sportCentersAdapter = createSportCenterAdapter()
        sportCentersRV.adapter = sportCentersAdapter

        /* shimmerFrameLayout INITIALIZER */
        sportCentersShimmerView = view.findViewById(R.id.sportCentersShimmerView)

        /* EXISTING RESERVATION INITIALIZER*/
        existingReservationCL = view.findViewById(R.id.existingReservationCL)
        navigateToReservationBTN = view.findViewById(R.id.navigateToReservationBTN)
    }

    private fun createServiceAdapter(): ServiceAdapter {
        return ServiceAdapter(
            viewModel.allServices,
            { viewModel.addServiceIdToFilters(it) },
            { viewModel.removeServiceIdFromFilters(it) },
            { viewModel.isServiceNameInList(it) }
        )
    }

    private fun createSportCenterAdapter(): SportCenterAdapter {
        return SportCenterAdapter(
            viewModel.getFilteredSportCenters(),
            viewModel.sportCenterReviews,
            { serviceName ->
                viewModel.isServiceNameInList(serviceName)
            },
            { sportCenterId, sportCenterName, sportCenterAddress, sportCenterPhoneNumber ->
                val direction =
                    SearchSportCentersFragmentDirections.actionSearchSportCentersToSearchCourts(
                        sportCenterId,
                        sportCenterName,
                        sportCenterAddress,
                        sportCenterPhoneNumber,
                        viewModel.getSelectedSportName(),
                        viewModel.selectedDateTimeMillis.value ?: 0,
                    )
                findNavController().navigate(direction)
            }
        )
    }


    private fun loadExistingReservation() {
        /* EXISTING RESERVATION LOADING*/
        viewModel.fetchData()
        viewModel.loadingState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    existingReservationCL.makeGone()
                    sportCentersShimmerView.startShimmerAnimation(sportCentersRV)
                    servicesShimmerView.startShimmerAnimation(servicesRV)
                    courtTypeACTV.makeInvisible()
                    courtTypeMCV.makeInvisible()
                    selectedSportShimmerView.makeVisible()
                    selectedSportShimmerView.startShimmer()
                    numberOfSportCentersFoundTV.makeGone()
                    noSportCentersFoundTV.makeGone()
                }

                is UiState.Failure -> {
                    servicesShimmerView.stopShimmerAnimation(servicesRV)
                    servicesShimmerView.stopShimmer()
                    sportCentersShimmerView.stopShimmer()
                    sportCentersShimmerView.makeInvisible()
                    selectedSportShimmerView.stopShimmer()
                    selectedSportShimmerView.makeInvisible()
                    numberOfSportCentersFoundTV.makeVisible()
                    toast(state.error ?: "Unable to load requested information")
                }

                is UiState.Success -> {
                    val reservationFound = viewModel.reservation
                    if (reservationFound != null) {
                        servicesShimmerView.stopShimmer()
                        sportCentersShimmerView.stopShimmer()
                        selectedSportShimmerView.stopShimmer()
                        // The user already has a reservation for this date/time
                        visibilityChangesToShowExistingReservation()
                        navigateToReservationBTN.setOnClickListener {
                            findNavController().navigate(
                                SearchSportCentersFragmentDirections.actionSearchSportCentersToReservationDetails(
                                    reservationFound.id
                                )
                            )
                        }
                        return@observe
                    }
                    // The user is free for this date/time
                    visibilityChangesToHideExistingReservation()
                    searchSportCentersUtil.setAutoCompleteTextViewSport(
                        requireContext(),
                        viewModel.allSports,
                        courtTypeACTV,
                        viewModel.getSelectedSportName()
                    )
                    val filteredSportCenters = viewModel.getFilteredSportCenters()
                    val numberOfSportCentersFound = filteredSportCenters.size
                    numberOfSportCentersFoundTV.text = getString(
                        R.string.search_sport_center_results_info,
                        numberOfSportCentersFound,
                        if (numberOfSportCentersFound != 1) "s" else ""
                    )
                    sportCentersAdapter.updateCollection(filteredSportCenters)
                    if (numberOfSportCentersFound > 0) {
                        sportCentersRV.makeVisible()
                    } else {
                        noSportCentersFoundTV.makeVisible()
                        sportCentersRV.makeInvisible()
                    }
                }
            }
        }
    }

    private fun visibilityChangesToShowExistingReservation() {
        servicesRV.makeGone()
        servicesShimmerView.makeGone()

        sportCentersRV.makeInvisible()
        sportCentersShimmerView.makeInvisible()

        courtTypeACTV.makeInvisible()
        courtTypeMCV.makeInvisible()
        selectedSportShimmerView.stopShimmer()
        selectedSportShimmerView.makeInvisible()

        numberOfSportCentersFoundTV.makeGone()
        noSportCentersFoundTV.makeGone()

        existingReservationCL.makeVisible()
    }

    private fun visibilityChangesToHideExistingReservation(){
        courtTypeACTV.makeVisible()
        courtTypeMCV.makeVisible()
        existingReservationCL.makeGone()
        servicesShimmerView.stopShimmerAnimation(servicesRV)
        servicesAdapter.updateCollection(viewModel.allServices)
        sportCentersShimmerView.stopShimmer()
        selectedSportShimmerView.stopShimmer()
        selectedSportShimmerView.makeInvisible()
        sportCentersShimmerView.makeInvisible()
        numberOfSportCentersFoundTV.makeVisible()
    }
    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return if (nextAnim != 0) {
            val anim: Animation = AnimationUtils.loadAnimation(activity, nextAnim)
            anim.setAnimationListener(object : AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    if (enter) {
                        numberOfSportCentersFoundTV.makeGone()
                        noSportCentersFoundTV.makeGone()
                        existingReservationCL.makeGone()
                        courtTypeACTV.makeInvisible()
                        courtTypeMCV.makeInvisible()
                        servicesShimmerView.startShimmerAnimation(servicesRV)
                        sportCentersShimmerView.startShimmerAnimation(sportCentersRV)
                        selectedSportShimmerView.makeVisible()
                        selectedSportShimmerView.startShimmer()
                    }
                }

                override fun onAnimationRepeat(animation: Animation) {
                    //unuseful
                }

                override fun onAnimationEnd(animation: Animation) {
                    if (enter) {
                        numberOfSportCentersFoundCL.layoutTransition = LayoutTransition()
                    }
                }
            })
            return anim
        } else {
            if (enter) {
                numberOfSportCentersFoundCL.layoutTransition = LayoutTransition()
            }
            null
        }
    }

    override fun onResume() {
        super.onResume()
        hideActionBar(activity)
        val minDateTime = SearchSportCentersUtils.getMockInitialDateTime()
        if (viewModel.selectedDateTimeMillis.value!! < minDateTime) {
            viewModel.changeSelectedDateTimeMillis(minDateTime)
        }
    }

}
