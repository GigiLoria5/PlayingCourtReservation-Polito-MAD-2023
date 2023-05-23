package it.polito.mad.g26.playingcourtreservation.fragment.searchFragments

import android.animation.LayoutTransition
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.adapter.searchCourtAdapters.ServiceAdapter
import it.polito.mad.g26.playingcourtreservation.adapter.searchCourtAdapters.SportCenterAdapter
import it.polito.mad.g26.playingcourtreservation.util.HorizontalSpaceItemDecoration
import it.polito.mad.g26.playingcourtreservation.util.SearchSportCentersUtil
import it.polito.mad.g26.playingcourtreservation.util.hideActionBar
import it.polito.mad.g26.playingcourtreservation.util.makeGone
import it.polito.mad.g26.playingcourtreservation.util.makeInvisible
import it.polito.mad.g26.playingcourtreservation.util.makeVisible
import it.polito.mad.g26.playingcourtreservation.util.startShimmerAnimation
import it.polito.mad.g26.playingcourtreservation.util.stopShimmerAnimation
import it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments.SearchSportCentersVM

class SearchSportCentersFragment : Fragment(R.layout.fragment_search_sport_centers) {

    private val args: SearchSportCentersFragmentArgs by navArgs()
    private val vm by viewModels<SearchSportCentersVM>()
    private val loadTime: Long = 500

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
    private lateinit var numberOfSportCentersFoundCL: ConstraintLayout
    private lateinit var numberOfSportCentersFoundTV: TextView

    private lateinit var sportCentersAdapter: SportCenterAdapter
    private lateinit var servicesAdapter: ServiceAdapter
    private lateinit var navigateToReservationBTN: Button

    /* LOGIC OBJECT OF THIS FRAGMENT */
    private val searchSportCentersUtil = SearchSportCentersUtil
    private var goingToSearchCourt = false

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

        /* VM INITIALIZATIONS */
        vm.initialize(city, dateTime, sportId, selectedServicesIds)

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
                vm.selectedDateTimeMillis.value!!,
                vm.getSelectedSportId(),
                vm.getSelectedServices()
            )
        }

        customToolBar.setOnClickListener {
            searchSportCentersUtil.navigateToAction(
                findNavController(),
                city,
                vm.selectedDateTimeMillis.value!!,
                vm.getSelectedSportId(),
                vm.getSelectedServices()
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
                getString(R.string.date_format),
                getString(R.string.hour_format),
                dateTV,
                hourTV
            )
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
            vm.services.value ?: listOf(),
            { vm.addServiceIdToFilters(it) },
            { vm.removeServiceIdFromFilters(it) },
            { vm.isServiceIdInList(it) }
        )
    }

    private fun createSportCenterAdapter(): SportCenterAdapter {
        return SportCenterAdapter(
            vm.getSportCentersWithDetailsFormatted(),
            { serviceId ->
                vm.isServiceIdInList(serviceId)
            },
            { sportCenterId, sportCenterName, sportCenterAddress, sportCenterPhoneNumber ->
                goingToSearchCourt = true
                val direction =
                    SearchSportCentersFragmentDirections.actionSearchSportCentersToSearchCourts(
                        sportCenterId,
                        sportCenterName,
                        sportCenterAddress,
                        sportCenterPhoneNumber,
                        vm.getSelectedSportId(),
                        courtTypeACTV.text.toString(),
                        vm.selectedDateTimeMillis.value ?: 0,
                    )
                findNavController().navigate(direction)
            }
        )
    }

    private fun initialLoading() {
        /* INITIAL LOADING */
        vm.selectedDateTimeMillis.observe(viewLifecycleOwner) {
            courtTypeACTV.makeVisible()
            courtTypeMCV.makeVisible()
            numberOfSportCentersFoundTV.makeGone()
            noSportCentersFoundTV.makeGone()
            existingReservationCL.makeGone()
            servicesShimmerView.startShimmerAnimation(servicesRV)
            sportCentersShimmerView.startShimmerAnimation(sportCentersRV)
        }
    }

    private fun loadExistingReservation() {
        /* EXISTING RESERVATION LOADING*/

        vm.existingReservationIdByDateAndTime.observe(viewLifecycleOwner) {
            //DO NOT USE it, TAKE THE CURRENT VALUE.
            // DUE TO AVOID DELAY PROBLEMS. it USES THE VALUE RECEIVED BEFORE loadTime IS ELAPSED
            Handler(Looper.getMainLooper()).postDelayed({
                if (vm.existingReservationIdByDateAndTime.value != null) {
                    servicesShimmerView.stopShimmer()
                    sportCentersShimmerView.stopShimmer()
                    showExistingReservationCL()
                    navigateToReservationBTN.setOnClickListener { _ ->
                        findNavController().navigate(
                            SearchSportCentersFragmentDirections.actionSearchSportCentersToReservationDetails(
                                vm.existingReservationIdByDateAndTime.value!!
                            )
                        )
                    }
                } else
                    hideExistingReservationCL()
            }, loadTime)
        }

    }

    private fun loadServices() {
        /* SERVICES LOADING */

        vm.services.observe(viewLifecycleOwner) {
            if (vm.existingReservationIdByDateAndTime.value == null) {
                Handler(Looper.getMainLooper()).postDelayed({
                    servicesShimmerView.stopShimmerAnimation(servicesRV)
                    servicesAdapter.updateCollection(vm.services.value ?: listOf())
                }, loadTime)
            }
        }
    }

    private fun loadSportCenters() {
        /* SPORT CENTERS LOADING */

        vm.sportCentersMediator.observe(viewLifecycleOwner) {
            if (vm.existingReservationIdByDateAndTime.value == null) {
                if (!sportCentersShimmerView.isShimmerStarted) {
                    sportCentersShimmerView.startShimmerAnimation(sportCentersRV)
                    numberOfSportCentersFoundTV.makeGone()
                    noSportCentersFoundTV.makeGone()
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    sportCentersShimmerView.stopShimmer()
                    sportCentersShimmerView.makeInvisible()
                    numberOfSportCentersFoundTV.makeVisible()

                    val sportCentersWithDetailsFormatted =
                        vm.getSportCentersWithDetailsFormatted()
                    val numberOfSportCentersFound =
                        sportCentersWithDetailsFormatted.size
                    numberOfSportCentersFoundTV.text = getString(
                        R.string.search_sport_center_results_info,
                        numberOfSportCentersFound,
                        if (numberOfSportCentersFound != 1) "s" else ""
                    )
                    sportCentersAdapter.updateCollection(
                        sportCentersWithDetailsFormatted
                    )
                    if (numberOfSportCentersFound > 0) {
                        sportCentersRV.makeVisible()
                    } else {
                        noSportCentersFoundTV.makeVisible()
                        sportCentersRV.makeInvisible()
                    }
                }, loadTime)
            }
        }
    }


    private fun showExistingReservationCL() {
        servicesRV.makeGone()
        servicesShimmerView.makeGone()

        sportCentersRV.makeInvisible()
        sportCentersShimmerView.makeInvisible()

        courtTypeACTV.makeInvisible()
        courtTypeMCV.makeInvisible()

        numberOfSportCentersFoundTV.makeGone()
        noSportCentersFoundTV.makeGone()

        existingReservationCL.makeVisible()

    }

    private fun hideExistingReservationCL() {

        courtTypeACTV.makeVisible()
        courtTypeMCV.makeVisible()

        existingReservationCL.makeGone()

    }


    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return if (nextAnim != 0) {
            val anim: Animation = AnimationUtils.loadAnimation(activity, nextAnim)
            anim.setAnimationListener(object : AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    if (!goingToSearchCourt) {
                        numberOfSportCentersFoundTV.makeGone()
                        noSportCentersFoundTV.makeGone()
                        existingReservationCL.makeGone()
                        servicesRV.makeInvisible()
                        sportCentersRV.makeInvisible()
                    }
                }

                override fun onAnimationRepeat(animation: Animation) {
                    //unuseful
                }
                override fun onAnimationEnd(animation: Animation) {
                    initialLoading()
                    loadExistingReservation()
                    loadServices()
                    loadSportCenters()
                    numberOfSportCentersFoundCL.layoutTransition = LayoutTransition()
                }
            })
            return anim
        } else {
            if (enter) {
                initialLoading()
                loadExistingReservation()
                loadServices()
                loadSportCenters()
                numberOfSportCentersFoundCL.layoutTransition = LayoutTransition()
            }
            null
        }
    }

    override fun onResume() {
        super.onResume()
        goingToSearchCourt = false
        hideActionBar(activity)
        val minDateTime = SearchSportCentersUtil.getMockInitialDateTime()
        if (vm.selectedDateTimeMillis.value!! < minDateTime) {
            vm.changeSelectedDateTimeMillis(minDateTime)
        }
    }
}