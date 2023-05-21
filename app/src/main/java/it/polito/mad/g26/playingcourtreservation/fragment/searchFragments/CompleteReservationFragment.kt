package it.polito.mad.g26.playingcourtreservation.fragment.searchFragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
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
import it.polito.mad.g26.playingcourtreservation.adapter.searchCourtAdapters.ChooseAvailableServiceAdapter
import it.polito.mad.g26.playingcourtreservation.util.SearchSportCentersUtil
import it.polito.mad.g26.playingcourtreservation.util.hideActionBar
import it.polito.mad.g26.playingcourtreservation.util.makeGone
import it.polito.mad.g26.playingcourtreservation.util.makeInvisible
import it.polito.mad.g26.playingcourtreservation.util.makeVisible
import it.polito.mad.g26.playingcourtreservation.util.startShimmerAnimation
import it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments.CompleteReservationVM

class CompleteReservationFragment : Fragment(R.layout.fragment_complete_reservation) {

    private val args: CompleteReservationFragmentArgs by navArgs()
    private val vm by viewModels<CompleteReservationVM>()
    private val loadTime: Long = 300

    /*   VISUAL COMPONENTS       */
    private lateinit var customToolBar: Toolbar
    private lateinit var sportCenterNameTV: TextView
    private lateinit var sportCenterAddressTV: TextView
    private lateinit var selectedDateTimeTV: TextView
    private lateinit var selectedCourtTV: TextView
    private lateinit var selectedCourtPriceTV: TextView
    private lateinit var selectedSportTV: TextView
    private lateinit var sportCenterPhoneNumberMCV: MaterialCardView
    private lateinit var numberOfAvailableServicesTV: TextView
    private lateinit var totalPriceTV: TextView
    private lateinit var chooseAvailableServicesRV: RecyclerView
    private lateinit var chooseAvailableServicesShimmerView: ShimmerFrameLayout
    private lateinit var chooseAvailableServicesAdapter: ChooseAvailableServiceAdapter
    private lateinit var noAvailableServicesFoundTV: TextView
    private lateinit var confirmBTN: Button
    /* ARGS */

    private var sportCenterId: Int = 0
    private var sportCenterName: String = ""
    private var sportCenterAddress: String = ""
    private var sportCenterPhoneNumber: String = ""
    private var courtId: Int = 0
    private var courtName: String = ""
    private var courtHourCharge: Float = 0f
    private var sportName: String = ""
    private var dateTime: Long = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sportCenterId = args.sportCenterId
        sportCenterName = args.sportCenterName
        sportCenterAddress = args.sportCenterAddress
        sportCenterPhoneNumber = args.sportCenterPhoneNumber
        courtId = args.courtId
        courtName = args.courtName
        courtHourCharge = args.courtHourCharge
        sportName = args.sportName
        dateTime = args.dateTime

        /* VM INITIALIZATIONS */
        vm.initialize(sportCenterId, courtId, courtHourCharge, dateTime)

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
        selectedCourtTV = view.findViewById(R.id.selectedCourtTV)
        selectedCourtPriceTV = view.findViewById(R.id.selectedCourtPriceTV)
        selectedSportTV = view.findViewById(R.id.selectedSportTV)
        sportCenterPhoneNumberMCV = view.findViewById(R.id.sportCenterPhoneNumberMCV)
        numberOfAvailableServicesTV = view.findViewById(R.id.numberOfAvailableServicesTV)
        totalPriceTV = view.findViewById(R.id.totalPriceTV)
        noAvailableServicesFoundTV = view.findViewById(R.id.noAvailableServicesFoundTV)
        confirmBTN = view.findViewById(R.id.confirmBTN)


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
        selectedCourtTV.text = courtName
        selectedCourtPriceTV.text =
            getString(R.string.hour_charge_court_short, String.format("%.2f", courtHourCharge))
        selectedSportTV.text = sportName

        sportCenterPhoneNumberMCV.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$sportCenterPhoneNumber")
            startActivity(intent)
        }


        /* COURTS RECYCLE VIEW INITIALIZER*/
        chooseAvailableServicesRV = view.findViewById(R.id.chooseAvailableServicesRV)
        chooseAvailableServicesAdapter = createChooseAvailableServicesAdapter()
        chooseAvailableServicesRV.adapter = chooseAvailableServicesAdapter

        /* shimmerFrameLayout INITIALIZER */
        chooseAvailableServicesShimmerView =
            view.findViewById(R.id.chooseAvailableServicesShimmerView)

        vm.totalAmountLiveData.observe(viewLifecycleOwner) {
            totalPriceTV.text = getString(
                R.string.just_price,
                String.format("%.2f", it)
            )
        }

        confirmBTN.setOnClickListener {
            if (dateTime < SearchSportCentersUtil.getMockInitialDateTime()) {
                Toast.makeText(
                    context,
                    R.string.too_late_for_time_slot,
                    Toast.LENGTH_LONG
                ).show()
            } else {
                vm.reserveCourt()
                findNavController().popBackStack()
                findNavController().popBackStack()
                Toast.makeText(
                    context,
                    getString(
                        R.string.confirm_reservation_message,
                        sportCenterName
                    ),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun createChooseAvailableServicesAdapter(): ChooseAvailableServiceAdapter {
        return ChooseAvailableServiceAdapter(
            vm.getServicesWithFees(),
            { vm.addServiceIdToFilters(it) },
            { vm.removeServiceIdFromFilters(it) },
            { vm.isServiceIdInList(it) }
        )
    }

    private fun loadAvailableServices() {
        /* SERVICES LOADING */

        vm.services.observe(viewLifecycleOwner) { services ->
            chooseAvailableServicesShimmerView.startShimmerAnimation(chooseAvailableServicesRV)
            numberOfAvailableServicesTV.makeGone()
            noAvailableServicesFoundTV.makeGone()

            Handler(Looper.getMainLooper()).postDelayed({
                chooseAvailableServicesShimmerView.stopShimmer()
                chooseAvailableServicesShimmerView.makeInvisible()
                numberOfAvailableServicesTV.makeVisible()
                val numberOfAvailableServicesFound =
                    services.size
                numberOfAvailableServicesTV.text = getString(
                    R.string.available_services_count_info,
                    numberOfAvailableServicesFound,
                    if (numberOfAvailableServicesFound != 1) "s" else ""
                )
                chooseAvailableServicesAdapter.updateCollection(vm.getServicesWithFees())
                if (numberOfAvailableServicesFound > 0) {
                    chooseAvailableServicesRV.makeVisible()
                } else {
                    noAvailableServicesFoundTV.makeVisible()
                    chooseAvailableServicesRV.makeInvisible()
                }
            }, loadTime)

        }
    }


    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return if (nextAnim != 0) {
            val anim: Animation = AnimationUtils.loadAnimation(activity, nextAnim)
            anim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    numberOfAvailableServicesTV.makeGone()
                }

                override fun onAnimationRepeat(animation: Animation) {
                    //unuseful
                }

                override fun onAnimationEnd(animation: Animation) {
                    loadAvailableServices()
                }
            })
            return anim
        } else
            null
    }


    override fun onResume() {
        super.onResume()
        hideActionBar(activity)
    }
}