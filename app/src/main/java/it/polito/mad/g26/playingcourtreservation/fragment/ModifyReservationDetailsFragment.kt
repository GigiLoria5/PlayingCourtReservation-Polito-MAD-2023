package it.polito.mad.g26.playingcourtreservation.fragment

import android.icu.util.Calendar
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.adapter.ModifyReservationDetailsAdapter
import it.polito.mad.g26.playingcourtreservation.model.Service
import it.polito.mad.g26.playingcourtreservation.model.custom.ServiceWithFee
import it.polito.mad.g26.playingcourtreservation.ui.CustomTextView
import it.polito.mad.g26.playingcourtreservation.util.ReservationWithDetailsUtil
import it.polito.mad.g26.playingcourtreservation.util.createCalendarObject
import it.polito.mad.g26.playingcourtreservation.util.setupActionBar
import it.polito.mad.g26.playingcourtreservation.util.takeIntCenterTime
import it.polito.mad.g26.playingcourtreservation.viewmodel.ReservationWithDetailsVM


class ModifyReservationXFragment : Fragment(R.layout.fragment_modify_reservation_details) {

    /*   VISUAL COMPONENTS       */
    private lateinit var dateMCV: MaterialCardView
    private lateinit var dateTV: TextView
    private lateinit var hourMCV: MaterialCardView
    private lateinit var hourTV: TextView

    private val reservationDetailsUtil = ReservationWithDetailsUtil

    private val args: ReservationDetailsFragmentArgs by navArgs()
    private val reservationWithDetailsVM by viewModels<ReservationWithDetailsVM>()
    private lateinit var servicesUsed: List<Service>
    private lateinit var servicesAll: List<ServiceWithFee>
    private lateinit var servicesChosen: MutableList<ServiceWithFee>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBar(activity, "Edit Reservation Details", true)

        var amount = mutableListOf(0.0f)
        var centerOpenTime = 16
        var centerCloseTime = 20

        //List of text
        val centerName = view.findViewById<CustomTextView>(R.id.sportCenter_name)
            .findViewById<TextView>(R.id.value)
        val centerTime=view.findViewById<CustomTextView>(R.id.sportCenter_time)
            .findViewById<TextView>(R.id.value)
        val field = view.findViewById<CustomTextView>(R.id.court_name)
            .findViewById<TextView>(R.id.value)
        val sport = view.findViewById<CustomTextView>(R.id.sport)
            .findViewById<TextView>(R.id.value)
        val city = view.findViewById<CustomTextView>(R.id.city)
            .findViewById<TextView>(R.id.value)
        val address = view.findViewById<CustomTextView>(R.id.address)
            .findViewById<TextView>(R.id.value)
        val price = view.findViewById<CustomTextView>(R.id.price)
            .findViewById<TextView>(R.id.value)
        val date = view.findViewById<CustomTextView>(R.id.date)
            .findViewById<TextView>(R.id.value)
        val time = view.findViewById<CustomTextView>(R.id.time)
            .findViewById<TextView>(R.id.value)
        dateTV = view.findViewById(R.id.dateTV)
        hourTV = view.findViewById(R.id.hourTV)
        val priceNew = view.findViewById<CustomTextView>(R.id.price_new)
            .findViewById<TextView>(R.id.value)
        val dateNew = view.findViewById<CustomTextView>(R.id.date_new)
            .findViewById<TextView>(R.id.value)
        val timeNew = view.findViewById<CustomTextView>(R.id.time_new)
            .findViewById<TextView>(R.id.value)

        // Retrieve Reservation Details
        val reservationId = args.reservationId
        reservationWithDetailsVM
            .getReservationWithDetailsById(reservationId)
            .observe(viewLifecycleOwner) { reservation ->
                //Compile TextView
                centerName.text = reservation.courtWithDetails.sportCenter.name
                centerTime.text=view.context.getString(
                    R.string.set_opening_hours,
                    reservation.courtWithDetails.sportCenter.openTime,
                    reservation.courtWithDetails.sportCenter.closeTime
                )
                field.text = reservation.courtWithDetails.court.name
                sport.text = reservation.courtWithDetails.sport.name
                city.text = reservation.courtWithDetails.sportCenter.city
                address.text = reservation.courtWithDetails.sportCenter.address
                date.text = reservation.reservation.date
                time.text = reservation.reservation.time
                price.text = view.context.getString(
                    R.string.set_text_with_euro,
                    reservation.reservation.amount.toString()
                )
                amount = mutableListOf(reservation.reservation.amount)
                dateNew.text = reservation.reservation.date
                timeNew.text = reservation.reservation.time
                priceNew.text = view.context.getString(
                    R.string.set_text_with_euro,
                    reservation.reservation.amount.toString()
                )

                //Variables
                val dateDayReservation = createCalendarObject(
                    reservation.reservation.date,
                    reservation.reservation.time
                )
                centerOpenTime =
                    takeIntCenterTime(reservation.courtWithDetails.sportCenter.openTime)
                centerCloseTime =
                    takeIntCenterTime(reservation.courtWithDetails.sportCenter.closeTime)

                //Select date of reservation as initial date
                reservationWithDetailsVM.changeSelectedDateTimeMillis(dateDayReservation.timeInMillis)

                //List of service used
                servicesUsed = reservation.services

                reservationWithDetailsVM.getAllServicesWithFee(reservation.courtWithDetails.sportCenter.id)
                    .observe(viewLifecycleOwner) { listOfServicesWithSportCenter ->
                        //List of services with fee of the sport center

                        reservationWithDetailsVM.getAllServices()
                            .observe(viewLifecycleOwner) { listService ->

                                //List of ServiceWithFee of that SportCenter
                                servicesAll = reservationWithDetailsVM.allServiceWithoutSport(
                                    listOfServicesWithSportCenter,
                                    listService
                                )

                                //List of service chosen
                                servicesChosen = reservationWithDetailsVM.filterServicesWithFee(
                                    servicesAll,
                                    servicesUsed
                                ).toMutableList()

                                //Recycler view of services
                                val recyclerView =
                                    view.findViewById<RecyclerView>(R.id.recyclerView_chip)
                                recyclerView.adapter =
                                    ModifyReservationDetailsAdapter(
                                        servicesAll,
                                        servicesChosen,
                                        priceNew,
                                        amount
                                    )
                                recyclerView.layoutManager = GridLayoutManager(context, 2)
                            }
                    }
            }

        /*ALERT DIALOG FOR SUCCESSFUL UPDATE*/
        val builderUpdated = AlertDialog.Builder(requireContext(), R.style.MyAlertDialogStyle)
        builderUpdated.setMessage("The reservation has been update successfully")
        builderUpdated.setPositiveButton("Ok") { _, _ ->
            // User clicked OK button
        }

        /*MENU CUSTOMIZATION*/
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.edit_profile_menu, menu)
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                // Handle the menu selection
                return when (item.itemId) {
                    // Back
                    android.R.id.home -> {
                        findNavController().popBackStack()
                        true
                    }
                    // Confirm Changes
                    R.id.confirm_menu_item -> {
                        //Take Ids of services
                        val idsServices =
                            reservationWithDetailsVM.getListOfIdService(servicesChosen)
                        //Date changes
                        val newDate = dateNew.text.toString()
                        reservationWithDetailsVM.updateReservation(
                            newDate,
                            hourTV.text.toString(),
                            reservationId,
                            idsServices,
                            amount[0]
                        )
                        builderUpdated.show()
                        //navigate back because id will be the same
                        findNavController().popBackStack()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        /* DATE MATERIAL CARD VIEW MANAGEMENT*/
        dateMCV = view.findViewById(R.id.dateMCV)
        dateMCV.setOnClickListener {
            reservationDetailsUtil.showDatePickerDialog(
                requireContext(),
                reservationWithDetailsVM,
                reservationId,
                hourTV,
                viewLifecycleOwner,
                dateNew,
                centerCloseTime
            )
        }

        /* HOUR MATERIAL CARD VIEW MANAGEMENT*/
        hourMCV = view.findViewById(R.id.hourMCV)
        hourMCV.setOnClickListener {
            reservationDetailsUtil.showNumberPickerDialog(
                requireContext(),
                reservationWithDetailsVM,
                centerOpenTime,
                centerCloseTime,
                reservationId,
                dateTV,
                viewLifecycleOwner,
                timeNew
            )

        }

        reservationWithDetailsVM.selectedDateTimeMillis.observe(viewLifecycleOwner) {
            //never used c?
            val c = Calendar.getInstance()
            c.timeInMillis = reservationWithDetailsVM.selectedDateTimeMillis.value!!
            reservationDetailsUtil.setDateTimeTextViews(
                reservationWithDetailsVM.selectedDateTimeMillis.value ?: 0,
                getString(R.string.dateFormat),
                getString(R.string.hourFormat),
                dateTV,
                hourTV,
                timeNew
            )
        }
    }
}
