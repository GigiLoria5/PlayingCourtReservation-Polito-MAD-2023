package it.polito.mad.g26.playingcourtreservation.fragment.searchFragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.adapter.searchCourtAdapters.ChooseAvailableServiceAdapter
import it.polito.mad.g26.playingcourtreservation.util.SearchSportCentersUtil
import it.polito.mad.g26.playingcourtreservation.util.UiState
import it.polito.mad.g26.playingcourtreservation.util.hideActionBar
import it.polito.mad.g26.playingcourtreservation.util.makeGone
import it.polito.mad.g26.playingcourtreservation.util.makeInvisible
import it.polito.mad.g26.playingcourtreservation.util.makeVisible
import it.polito.mad.g26.playingcourtreservation.util.startShimmerAnimation
import it.polito.mad.g26.playingcourtreservation.util.toast
import it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments.CompleteReservationViewModel

@AndroidEntryPoint
class CompleteReservationFragment : Fragment(R.layout.complete_reservation_fragment) {

    private val args: CompleteReservationFragmentArgs by navArgs()
    private val viewModel by viewModels<CompleteReservationViewModel>()

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
    private var sportCenterId: String = ""
    private var sportCenterName: String = ""
    private var sportCenterAddress: String = ""
    private var sportCenterPhoneNumber: String = ""
    private var courtId: String = ""
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
        viewModel.initialize(sportCenterId, courtId, courtHourCharge, dateTime)

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
                getString(R.string.hour_format)
            ),
            SearchSportCentersUtil.getDateTimeFormatted(
                dateTime,
                getString(R.string.date_extended_format)
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

        viewModel.totalAmount.observe(viewLifecycleOwner) {
            totalPriceTV.text = getString(
                R.string.just_total_reservation_price,
                String.format("%.2f", it)
            )
        }

        // Confirm Button
        confirmBTN.setOnClickListener {
            if (dateTime < SearchSportCentersUtil.getMockInitialDateTime()) {
                Toast.makeText(
                    context,
                    R.string.too_late_for_time_slot,
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            viewModel.reserveCourt()
        }

        // Reservation Process Handling
        viewModel.reserveCourtState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    // TODO: Add loading animation
                    println("Reserving court...")
                }

                is UiState.Failure -> {
                    toast(state.error ?: "Unable to reserve court")
                }

                is UiState.Success -> {
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
    }

    private fun createChooseAvailableServicesAdapter(): ChooseAvailableServiceAdapter {
        return ChooseAvailableServiceAdapter(
            listOf(),
            { viewModel.addSelectedService(it) },
            { viewModel.removeSelectedService(it) },
            { viewModel.isServiceInList(it) }
        )
    }

    private fun loadAvailableServices() {
        /* SERVICES LOADING */
        viewModel.getServices()
        viewModel.services.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    chooseAvailableServicesShimmerView.startShimmerAnimation(
                        chooseAvailableServicesRV
                    )
                    numberOfAvailableServicesTV.makeGone()
                    noAvailableServicesFoundTV.makeGone()
                }

                is UiState.Failure -> {
                    chooseAvailableServicesShimmerView.stopShimmer()
                    chooseAvailableServicesShimmerView.makeInvisible()
                    toast(state.error ?: "Unable to get services")
                }

                is UiState.Success -> {
                    chooseAvailableServicesShimmerView.stopShimmer()
                    chooseAvailableServicesShimmerView.makeInvisible()
                    numberOfAvailableServicesTV.makeVisible()
                    val numberOfAvailableServicesFound = state.result.size
                    numberOfAvailableServicesTV.text = getString(
                        R.string.available_services_count_info,
                        numberOfAvailableServicesFound,
                        if (numberOfAvailableServicesFound != 1) "s" else ""
                    )
                    chooseAvailableServicesAdapter.updateCollection(state.result)
                    if (numberOfAvailableServicesFound > 0) {
                        chooseAvailableServicesRV.makeVisible()
                    } else {
                        noAvailableServicesFoundTV.makeVisible()
                        chooseAvailableServicesRV.makeInvisible()
                    }
                }
            }
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