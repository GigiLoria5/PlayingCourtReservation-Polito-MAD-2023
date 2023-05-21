package it.polito.mad.g26.playingcourtreservation.fragment.searchFragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.card.MaterialCardView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.adapter.searchCourtAdapters.CourtAdapter
import it.polito.mad.g26.playingcourtreservation.util.SearchSportCentersUtil
import it.polito.mad.g26.playingcourtreservation.util.hideActionBar
import it.polito.mad.g26.playingcourtreservation.util.makeGone
import it.polito.mad.g26.playingcourtreservation.util.makeInvisible
import it.polito.mad.g26.playingcourtreservation.util.makeVisible
import it.polito.mad.g26.playingcourtreservation.util.startShimmerAnimation
import it.polito.mad.g26.playingcourtreservation.util.stopShimmerAnimation
import it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments.SearchCourtsVM

class SearchCourtsFragment : Fragment(R.layout.fragment_search_courts) {

    private val args: SearchCourtsFragmentArgs by navArgs()
    private val vm by viewModels<SearchCourtsVM>()
    private val loadTime:Long=500

    /*   VISUAL COMPONENTS       */
    private lateinit var customToolBar: Toolbar
    private lateinit var sportCenterNameTV: TextView
    private lateinit var sportCenterAddressTV: TextView
    private lateinit var selectedDateTimeTV: TextView
    private lateinit var selectedSportTV: TextView
    private lateinit var numberOfAvailableCourtsTV: TextView
    private lateinit var sportCenterPhoneNumberMCV: MaterialCardView

    private lateinit var courtsRV: RecyclerView
    private lateinit var courtsShimmerView: ShimmerFrameLayout
    private lateinit var courtsAdapter: CourtAdapter
    /* SUPPORT VARIABLES */

    private var goingToCompleteReservation = false
    private var goingToCourtReviews = false

    /* ARGS */
    private var sportCenterId: Int = 0
    private var sportCenterName: String = ""
    private var sportCenterAddress: String = ""
    private var sportCenterPhoneNumber: String = ""
    private var sportId: Int = 0
    private var sportName: String = ""
    private var dateTime: Long = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sportCenterId = args.sportCenterId
        sportCenterName = args.sportCenterName
        sportCenterAddress = args.sportCenterAddress
        sportCenterPhoneNumber = args.sportCenterPhoneNumber
        sportId = args.sportId
        sportId = args.sportId
        sportName = args.sportName
        dateTime = args.dateTime

        /* VM INITIALIZATIONS */
        vm.initialize(sportCenterId,sportId,dateTime)

        /* CUSTOM TOOLBAR MANAGEMENT*/
        customToolBar = view.findViewById(R.id.customToolBar)
        customToolBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        /*BACK BUTTON MANAGEMENT*/
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        /* INIT VISUAL COMPONENTS */
        sportCenterNameTV = view.findViewById(R.id.sportCenterNameTV)
        sportCenterAddressTV = view.findViewById(R.id.sportCenterAddressTV)
        selectedDateTimeTV = view.findViewById(R.id.selectedDateTimeTV)
        selectedSportTV = view.findViewById(R.id.selectedSportTV)
        numberOfAvailableCourtsTV = view.findViewById(R.id.numberOfAvailableCourtsTV)
        sportCenterPhoneNumberMCV = view.findViewById(R.id.sportCenterPhoneNumberMCV)

        /* SET VALUES TO VISUAL COMPONENTS*/
        sportCenterNameTV.text = sportCenterName
        sportCenterAddressTV.text = sportCenterAddress
        selectedDateTimeTV.text = getString(
            R.string.selected_date_time_res,
            SearchSportCentersUtil.getDateTimeFormatted(
                dateTime,
                getString(R.string.hourFormat)
            ),
            SearchSportCentersUtil.getDateTimeFormatted(
                dateTime,
                getString(R.string.dateExtendedFormat)
            )
        )
        selectedSportTV.text = sportName


        sportCenterPhoneNumberMCV.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$sportCenterPhoneNumber")
            startActivity(intent)
        }

        /* COURTS RECYCLE VIEW INITIALIZER*/
        courtsRV = view.findViewById(R.id.courtsRV)
        courtsAdapter = createCourtAdapter()
        courtsRV.adapter = courtsAdapter

        /* shimmerFrameLayout INITIALIZER */
        courtsShimmerView = view.findViewById(R.id.courtsShimmerView)

    }

    private fun createCourtAdapter(): CourtAdapter {
        return CourtAdapter(
            vm.getCourtsBySelectedSport(),
            vm.reviews.value ?: listOf(),
            { vm.isCourtAvailable(it) },
            { courtId ->
                goingToCourtReviews = true
                val direction =
                    SearchCourtsFragmentDirections.actionSearchCourtsToCourtReviews(
                        courtId
                    )
                findNavController().navigate(direction)
            }
        ) { courtId, courtName, courtHourCharge, sportName ->
            if (dateTime < SearchSportCentersUtil.getMockInitialDateTime()) {
                findNavController().popBackStack()
                Toast.makeText(
                    context,
                    R.string.too_late_for_time_slot,
                    Toast.LENGTH_LONG
                ).show()
            } else {
                goingToCompleteReservation = true
                val direction =
                    SearchCourtsFragmentDirections.actionSearchCourtsFragmentToCompleteReservationFragment(
                        sportCenterId,
                        sportCenterName,
                        sportCenterAddress,
                        sportCenterPhoneNumber,
                        courtId,
                        courtName,
                        courtHourCharge,
                        sportName,
                        dateTime
                    )
                findNavController().navigate(direction)
            }
        }
    }

    private fun loadCourts() {

        /* COURTS LOADING */
        vm.reviews.observe(viewLifecycleOwner) { it ->
            courtsShimmerView.startShimmerAnimation(courtsRV)
            numberOfAvailableCourtsTV.makeGone()

            Handler(Looper.getMainLooper()).postDelayed({
                courtsShimmerView.stopShimmerAnimation(courtsRV)
                val numberOfAvailableCourts = vm.getTotAvailableCourts()
                numberOfAvailableCourtsTV.text = getString(
                    R.string.search_courts_results_info,
                    numberOfAvailableCourts,
                    if (numberOfAvailableCourts != 1) "s" else ""
                )
                numberOfAvailableCourtsTV.makeVisible()

                courtsAdapter.updateCollection(vm.getCourtsBySelectedSport(), it)
            }, loadTime)
        }
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return if (nextAnim != 0) {
            val anim: Animation = AnimationUtils.loadAnimation(activity, nextAnim)
            anim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    if (!(goingToCompleteReservation || goingToCourtReviews)) {
                        numberOfAvailableCourtsTV.makeGone()
                        courtsRV.makeInvisible()
                    }
                }

                override fun onAnimationRepeat(animation: Animation) {
                    //unuseful
                }

                override fun onAnimationEnd(animation: Animation) {
                    loadCourts()
                }
            })
            anim
        } else
            null
    }

    override fun onResume() {
        super.onResume()
        goingToCompleteReservation = false
        goingToCourtReviews = false
        hideActionBar(activity)
    }
}