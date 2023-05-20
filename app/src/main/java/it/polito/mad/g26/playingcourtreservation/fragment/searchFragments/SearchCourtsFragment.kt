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
import it.polito.mad.g26.playingcourtreservation.model.CourtWithDetails
import it.polito.mad.g26.playingcourtreservation.model.SportCenter
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

    /*   VISUAL COMPONENTS       */
    private lateinit var customToolBar: Toolbar
    private lateinit var sportCenterNameTV: TextView
    private lateinit var sportCenterAddressTV: TextView
    private lateinit var selectedDateTimeTV: TextView
    private lateinit var selectedSportTV: TextView
    private lateinit var numberOfCourtsFoundTV: TextView
    private lateinit var sportCenterPhoneNumberMCV: MaterialCardView

    private lateinit var courtsRV: RecyclerView
    private lateinit var courtsShimmerView: ShimmerFrameLayout

    /* SUPPORT VARIABLES */

    private lateinit var sportCenter: SportCenter
    private lateinit var courts: List<CourtWithDetails>
    private var goingToSelectServices = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sportCenterId = args.sportCenterId
        val sportId = args.sportId

        /* VM INITIALIZATIONS */
        vm.setSportCenterId(sportCenterId)

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
        numberOfCourtsFoundTV = view.findViewById(R.id.numberOfCourtsFoundTV)
        sportCenterPhoneNumberMCV = view.findViewById(R.id.sportCenterPhoneNumberMCV)

        /* COURTS RECYCLE VIEW INITIALIZER*/
        courtsRV = view.findViewById(R.id.courtsRV)

        /* shimmerFrameLayout INITIALIZER */
        courtsShimmerView = view.findViewById(R.id.courtsShimmerView)


        vm.sportCenter.observe(viewLifecycleOwner) {
            sportCenter = it.sportCenter
            courts = it.courts

            sportCenterNameTV.text = sportCenter.name
            sportCenterAddressTV.text = getString(
                R.string.sport_center_address_res,
                sportCenter.address, sportCenter.city
            )
            selectedDateTimeTV.text = getString(
                R.string.selected_date_time_res,
                SearchSportCentersUtil.getDateTimeFormatted(
                    111,
                    getString(R.string.hourFormat)
                ),
                SearchSportCentersUtil.getDateTimeFormatted(111, getString(R.string.dateFormat))
            )
            if (sportId != 0)
                selectedSportTV.text = courts.first { it.sport.id == sportId }.sport.name

            val numberOfCourtsFound = courts.size
            numberOfCourtsFoundTV.text = getString(
                R.string.search_courts_results_info,
                numberOfCourtsFound,
                if (numberOfCourtsFound != 1) "s" else ""
            )

            sportCenterPhoneNumberMCV.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:${sportCenter.phoneNumber}")
                startActivity(intent)
            }
        }
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation {
        val anim: Animation = AnimationUtils.loadAnimation(activity, nextAnim)
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                if (!goingToSelectServices) {
                    numberOfCourtsFoundTV.makeGone()
                    courtsRV.makeInvisible()
                }
            }

            override fun onAnimationRepeat(animation: Animation) {
            }

            override fun onAnimationEnd(animation: Animation) {
                /* USE EXISTING VIEW */
                val view = view!!

                /* COURTS LOADING */
                vm.reviews.observe(viewLifecycleOwner) { it ->
                    courtsShimmerView.startShimmerAnimation(courtsRV)
                    numberOfCourtsFoundTV.makeGone()
                    Handler(Looper.getMainLooper()).postDelayed({
                        courtsShimmerView.stopShimmerAnimation(courtsRV)
                        numberOfCourtsFoundTV.makeVisible()
                        val courtsAdapter = CourtAdapter(
                            courts,
                            it,
                            { true },//todo fai funzione per controllare se court è occupato
                            { courtId ->
                                val direction =
                                    SearchCourtsFragmentDirections.actionSearchCourtsToCourtReviews(
                                        courtId
                                    )
                                findNavController().navigate(direction)
                            }
                        ) {}
                        courtsRV.adapter = courtsAdapter
                    }, 200)
                }
            }
        })
        return anim
    }

    override fun onResume() {
        super.onResume()
        goingToSelectServices = false
        hideActionBar(activity)
    }
}