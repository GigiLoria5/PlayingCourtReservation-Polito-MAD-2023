package it.polito.mad.g26.playingcourtreservation.fragment

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.adapter.ModifyReservationDetailsAdapter
import it.polito.mad.g26.playingcourtreservation.newModel.Service
import it.polito.mad.g26.playingcourtreservation.ui.CustomTextView
import it.polito.mad.g26.playingcourtreservation.util.HorizontalSpaceItemDecoration
import it.polito.mad.g26.playingcourtreservation.util.SearchSportCentersUtil
import it.polito.mad.g26.playingcourtreservation.util.UiState
import it.polito.mad.g26.playingcourtreservation.util.createCalendarObject
import it.polito.mad.g26.playingcourtreservation.util.hideActionBar
import it.polito.mad.g26.playingcourtreservation.util.takeIntCenterTime
import it.polito.mad.g26.playingcourtreservation.util.toast
import it.polito.mad.g26.playingcourtreservation.viewmodel.EditReservationDetailsViewModel

@AndroidEntryPoint
class EditReservationDetailsFragment : Fragment(R.layout.edit_reservation_details_fragment) {

    /*   VISUAL COMPONENTS       */
    private lateinit var dateMCV: MaterialCardView
    private lateinit var dateTV: TextView
    private lateinit var hourMCV: MaterialCardView
    private lateinit var hourTV: TextView

    /* LOGIC OBJECT OF THIS FRAGMENT */
    private val searchSportCentersUtil = SearchSportCentersUtil

    private val args: ReservationDetailsFragmentArgs by navArgs()
    private val viewModel by viewModels<EditReservationDetailsViewModel>()
    private lateinit var servicesUsed: List<String>
    private lateinit var servicesAll: List<Service>
    private lateinit var servicesChosen: MutableList<Service>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val reservationId = args.reservationId
        viewModel.initialize(reservationId)

        /* CUSTOM TOOLBAR MANAGEMENT*/
        val customToolBar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.customToolBar)
        customToolBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        /*BACK BUTTON MANAGEMENT*/
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        val customConfirmIconIV = view.findViewById<ImageView>(R.id.customConfirmIconIV)

        // TODO: update new time and new date
        // TODO: check open time and close time
        // TODO: check if user has already a reservation for that time

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
        }

        //List of text
        val centerName = view.findViewById<TextView>(R.id.sportCenter_name)
        val centerTime = view.findViewById<TextView>(R.id.sportCenter_time)
        val field = view.findViewById<TextView>(R.id.court_name)
        val sport = view.findViewById<TextView>(R.id.sport)
        val address = view.findViewById<TextView>(R.id.address)
        val price = view.findViewById<TextView>(R.id.price)
        val date = view.findViewById<TextView>(R.id.date)
        dateTV = view.findViewById(R.id.dateTV)
        hourTV = view.findViewById(R.id.hourTV)
        val priceNew = view.findViewById<CustomTextView>(R.id.price_new)
            .findViewById<TextView>(R.id.value)
        val dateNew = view.findViewById<CustomTextView>(R.id.date_new)
            .findViewById<TextView>(R.id.value)
        val timeNew = view.findViewById<CustomTextView>(R.id.time_new)
            .findViewById<TextView>(R.id.value)

        // Retrieve Reservation Details
        viewModel.loadReservationAndSportCenterInformation()
        viewModel.loadingState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    // TODO: Start Animation
                }

                is UiState.Failure -> {
                    // TODO: Stop Animation
                    toast(state.error ?: "Unable to get reservation details")
                }

                is UiState.Success -> {
                    val reservation = viewModel.reservation
                    val reservationSportCenter = viewModel.sportCenter
                    val reservationCourt = reservationSportCenter
                        .courts.first { it.id == reservation.courtId }
                    // Update UI components
                    centerName.text = reservationSportCenter.name
                    centerTime.text = view.context.getString(
                        R.string.set_opening_hours,
                        reservationSportCenter.openTime,
                        reservationSportCenter.closeTime
                    )
                    field.text = reservationCourt.name
                    sport.text = reservationCourt.sport
                    address.text =
                        getString(
                            R.string.sport_center_address_res,
                            reservationSportCenter.address,
                            reservationSportCenter.city
                        )
                    date.text = getString(
                        R.string.selected_date_time_res,
                        reservation.time,
                        reservation.date
                    )
                    price.text = view.context.getString(
                        R.string.set_text_with_euro,
                        reservation.amount.toString()
                    )
                    val amount = mutableListOf(reservation.amount)
                    dateNew.text = reservation.date
                    timeNew.text = reservation.time
                    priceNew.text = view.context.getString(
                        R.string.set_text_with_euro,
                        reservation.amount.toString()
                    )
                    // Variables
                    val dateDayReservation = createCalendarObject(
                        reservation.date,
                        reservation.time
                    )
                    val centerOpenTime =
                        takeIntCenterTime(reservationSportCenter.openTime)
                    val centerCloseTime =
                        takeIntCenterTime(reservationSportCenter.closeTime)

                    // Select date of reservation as initial date
                    viewModel.changeSelectedDateTimeMillis(dateDayReservation.timeInMillis)

                    // List of service used
                    servicesUsed = reservation.services

                    // List of ServiceWithFee of that SportCenter
                    servicesAll = reservationSportCenter.services

                    //List of service chosen
                    servicesChosen = servicesAll
                        .filter { servicesUsed.contains(it.name) }
                        .toMutableList()

                    //Recycler view of services
                    val recyclerView =
                        view.findViewById<RecyclerView>(R.id.recyclerView_chip)
                    val adapter =
                        ModifyReservationDetailsAdapter(
                            servicesAll,
                            servicesChosen,
                            priceNew,
                            amount
                        )
                    val itemDecoration =
                        HorizontalSpaceItemDecoration(
                            resources.getDimensionPixelSize(
                                R.dimen.chip_distance
                            )
                        )
                    recyclerView.addItemDecoration(itemDecoration)
                    recyclerView.adapter = adapter
                }
            }
        }

        // Handle update reservation
        customConfirmIconIV.setOnClickListener {
            val newReservation = viewModel.reservation
                .copy(services = servicesChosen.map { it.name })
            viewModel.updateReservation(newReservation)
        }

        viewModel.updateState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    // TODO: Start Animation
                }

                is UiState.Failure -> {
                    // TODO: Stop Animation
                    toast(state.error ?: "Unable to update reservation")
                }

                is UiState.Success -> {
                    // TODO: Stop Animation
                    Toast.makeText(
                        context,
                        R.string.reservation_success_update,
                        Toast.LENGTH_SHORT
                    ).show() //navigate back because id will be the same
                    findNavController().previousBackStackEntry?.savedStateHandle?.set(
                        "key",
                        viewModel.reservationId
                    )
                    findNavController().popBackStack()
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        hideActionBar(activity)
    }
}
