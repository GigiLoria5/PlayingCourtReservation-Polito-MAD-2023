package it.polito.mad.g26.playingcourtreservation.fragment


import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.adapter.ReservationDetailsAdapter
import it.polito.mad.g26.playingcourtreservation.adapter.searchCourtAdapters.ServiceWithFeeAdapter
import it.polito.mad.g26.playingcourtreservation.model.Service
import it.polito.mad.g26.playingcourtreservation.model.custom.ServiceWithFee
import it.polito.mad.g26.playingcourtreservation.util.HorizontalSpaceItemDecoration
import it.polito.mad.g26.playingcourtreservation.util.hideActionBar
import it.polito.mad.g26.playingcourtreservation.util.makeInvisible
import it.polito.mad.g26.playingcourtreservation.util.makeVisible
import it.polito.mad.g26.playingcourtreservation.viewmodel.ReservationWithDetailsVM


class ReservationDetailsFragment : Fragment(R.layout.reservation_details_fragment) {

    private val args: ReservationDetailsFragmentArgs by navArgs()
    private val reservationWithDetailsVM by viewModels<ReservationWithDetailsVM>()
    private lateinit var servicesAll: List<ServiceWithFee>
    private lateinit var servicesUsed: List<Service>
    private lateinit var servicesChosen: List<ServiceWithFee>
    private val today = Calendar.getInstance()
    private lateinit var reservationReviewMCV: MaterialCardView
    private lateinit var sportCenterPhoneNumberMCV: MaterialCardView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //List of text
        val centerName = view.findViewById<TextView>(R.id.sportCenter_name)
        val centerTime = view.findViewById<TextView>(R.id.sportCenter_time)
        val field = view.findViewById<TextView>(R.id.court_name)
        val sport = view.findViewById<TextView>(R.id.sport)
        val address = view.findViewById<TextView>(R.id.address)
        val price = view.findViewById<TextView>(R.id.price)
        val date = view.findViewById<TextView>(R.id.date)
        val serviceTitle = view.findViewById<TextView>(R.id.service_title)
        /* CUSTOM TOOLBAR MANAGEMENT*/
        val customToolBar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.customToolBar)
        customToolBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        val privateList = listOf(
            "profile1",
            "profile2",
            "profile3",
            "profile4",
            "profile5",
            "profile6",
            "profile7",
            "profile8",
            "profile9",
            "profile10",
            "profile11"
        )

        /*BACK BUTTON MANAGEMENT*/
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        sportCenterPhoneNumberMCV = view.findViewById(R.id.sportCenterPhoneNumberMCV)
        // Retrieve Reservation Details
        val reservationId = args.reservationId
        reservationReviewMCV = view.findViewById(R.id.reservationReviewMCV)

        reservationWithDetailsVM
            .getReservationWithDetailsById(reservationId)
            .observe(viewLifecycleOwner) { reservation ->
                servicesUsed = reservation.services
                centerName.text = reservation.courtWithDetails.sportCenter.name
                centerTime.text = view.context.getString(
                    R.string.set_opening_hours,
                    reservation.courtWithDetails.sportCenter.openTime,
                    reservation.courtWithDetails.sportCenter.closeTime
                )
                field.text = reservation.courtWithDetails.court.name
                sport.text = reservation.courtWithDetails.sport.name
                address.text =
                    getString(
                        R.string.sport_center_address_res,
                        reservation.courtWithDetails.sportCenter.address,
                        reservation.courtWithDetails.sportCenter.city
                    )

                date.text = getString(
                    R.string.selected_date_time_res,
                    reservation.reservation.time,
                    reservation.reservation.date
                )
                price.text = view.context.getString(
                    R.string.set_text_with_euro,
                    reservation.reservation.amount.toString()
                )

                sportCenterPhoneNumberMCV.setOnClickListener {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data =
                        Uri.parse("tel:${reservation.courtWithDetails.sportCenter.phoneNumber}")
                    startActivity(intent)
                }


                //List of variable
                val dateList = reservation.reservation.date.split("-")
                val yearRes = dateList[2].toInt()
                val monthRes = dateList[1].toInt()
                val dayRes = dateList[0].toInt()
                val timeList = reservation.reservation.time.split(":")
                val hourRes = timeList[0].toInt()
                val calendarRes = Calendar.getInstance().apply {
                    set(Calendar.YEAR, yearRes)
                    set(Calendar.MONTH, monthRes - 1)
                    set(Calendar.DAY_OF_MONTH, dayRes)
                    set(Calendar.HOUR_OF_DAY, hourRes - 1)
                    set(Calendar.MINUTE, 30)
                }

                //Alert Dialog
                val builder = AlertDialog.Builder(requireContext(), R.style.MyAlertDialogStyle)
                builder.setMessage("Are you sure you want to delete the reservation?")
                builder.setPositiveButton("Yes") { _, _ ->
                    // User clicked OK button
                    reservationWithDetailsVM.deleteReservationById(reservationId)
                    findNavController().popBackStack()
                }
                builder.setNegativeButton("No") { _, _ ->
                    // User cancelled the dialog
                }

                //View to be inflated with buttons
                val viewReservationButtons =
                    view.findViewById<ConstraintLayout>(R.id.reservation_buttons)

                //Show reservation buttons if future or review button if past
                if (today <= calendarRes) {
                    // Inflate the new layout with two buttons of reservation
                    val inflater = LayoutInflater.from(requireContext())
                    val viewDeleteAndEdit = inflater.inflate(
                        R.layout.reservation_details_delete_and_edit_buttons,
                        viewReservationButtons,
                        false
                    )
                    val deleteButton =
                        viewDeleteAndEdit.findViewById<MaterialButton>(R.id.delete_reservation_button)
                    val editButton =
                        viewDeleteAndEdit.findViewById<MaterialButton>(R.id.modify_reservation_button)
                    viewReservationButtons.addView(viewDeleteAndEdit)

                    deleteButton.setOnClickListener {
                        builder.show()
                    }
                    editButton.setOnClickListener {
                        val action =
                            ReservationDetailsFragmentDirections.openReservationEdit(reservationId)
                        findNavController().navigate(action)
                    }

                } else {
                    //Inflate with button of review
                    reservationWithDetailsVM.findReservationReview(reservationId, 1)
                        .observe(viewLifecycleOwner) { review ->
                            if (review == null) {
                                reservationReviewMCV.makeInvisible()
                                viewReservationButtons.removeAllViews()
                                val inflater = LayoutInflater.from(requireContext())
                                val viewAddReview = inflater.inflate(
                                    R.layout.reservation_details_add_review_button,
                                    viewReservationButtons,
                                    false
                                )
                                val reviewAddButton =
                                    viewAddReview.findViewById<MaterialButton>(R.id.add_review_button)
                                viewReservationButtons.addView(viewAddReview)
                                reviewAddButton.setOnClickListener {
                                    val addReviewDialog =
                                        CustomDialogAlertAddReview.newInstance(reservationId, 1)
                                    addReviewDialog.show(
                                        parentFragmentManager,
                                        CustomDialogAlertAddReview.TAG
                                    )
                                }
                            } else {
                                viewReservationButtons.removeAllViews()
                                val reviewEditButton =
                                    view.findViewById<MaterialButton>(R.id.modifyReviewButton)
                                val reviewDeleteButton =
                                    view.findViewById<MaterialButton>(R.id.deleteReviewButton)
                                reservationReviewMCV.makeVisible()
                                val rating = view.findViewById<RatingBar>(R.id.rating)
                                val reviewDate = view.findViewById<TextView>(R.id.reviewDateTV)
                                val reviewText = view.findViewById<TextView>(R.id.reviewTextTV)
                                rating.rating = review.rating
                                reviewDate.text = review.date
                                reviewText.text = review.text
                                reviewDeleteButton.setOnClickListener {
                                    //Alert Dialog
                                    val builderDeleteReview = AlertDialog.Builder(
                                        requireContext(),
                                        R.style.MyAlertDialogStyle
                                    )
                                    builderDeleteReview.setMessage("Are you sure you want to delete the review?")
                                    builderDeleteReview.setPositiveButton("Yes") { _, _ ->
                                        // User clicked OK button
                                        reservationWithDetailsVM.deleteReviewById(reservationId, 1)
                                        Toast.makeText(
                                            context,
                                            "Review deleted successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    builderDeleteReview.setNegativeButton("No") { _, _ ->
                                        // User cancelled the dialog
                                    }
                                    builderDeleteReview.show()
                                }
                                reviewEditButton.setOnClickListener {
                                    val addReviewDialog =
                                        CustomDialogAlertAddReview.newInstance(reservationId, 1)
                                    addReviewDialog.show(
                                        parentFragmentManager,
                                        CustomDialogAlertAddReview.TAG
                                    )
                                }
                            }
                        }

                }

                reservationWithDetailsVM.getAllServicesWithFee(reservation.courtWithDetails.sportCenter.id)
                    .observe(viewLifecycleOwner) { listOfServicesWithSportCenter ->
                        //List of services with fee of the sport center

                        reservationWithDetailsVM.getAllServices()
                            .observe(viewLifecycleOwner) { listService ->
                                //List of all services

                                //List of ServiceWithFee of that Sport center
                                servicesAll = reservationWithDetailsVM.allServiceWithoutSport(
                                    listOfServicesWithSportCenter,
                                    listService
                                )

                                //Filter service to take only chosen
                                servicesChosen = reservationWithDetailsVM.filterServicesWithFee(
                                    servicesAll,
                                    servicesUsed
                                )

                                //Recycler view of services
                                val recyclerView =
                                    view.findViewById<RecyclerView>(R.id.service_list)
                                if (servicesChosen.isEmpty())
                                    serviceTitle.text =
                                        getString(R.string.reservation_no_services_chosen)
                                else {
                                    val adapter = ServiceWithFeeAdapter(
                                        servicesChosen,
                                        isServiceIdInList = { false },
                                        isClickable = false
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
            }

        val partiRecyclerView = view.findViewById<RecyclerView>(R.id.player_list)
        val adapter = ReservationDetailsAdapter(privateList, 1)
        partiRecyclerView.adapter = adapter
        partiRecyclerView.layoutManager = GridLayoutManager(context, 2)
        //to avoid scrolling of recycler


        //see if work
        val partiRecyclerView2 = view.findViewById<RecyclerView>(R.id.requester_list)
        val adapter2 = ReservationDetailsAdapter(privateList, 2)
        partiRecyclerView2.adapter = adapter2
        partiRecyclerView2.layoutManager = LinearLayoutManager(context)
        //to avoid scrolling of recycler

        /*MENU ITEM*/
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.reservation_details, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    // Back
                    android.R.id.home -> {
                        findNavController().popBackStack()
                        true
                    }

                    else -> false
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onResume() {
        super.onResume()
        hideActionBar(activity)
    }
}