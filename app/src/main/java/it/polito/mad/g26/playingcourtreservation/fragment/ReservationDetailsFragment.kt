package it.polito.mad.g26.playingcourtreservation.fragment


import android.icu.util.Calendar
import android.os.Bundle
import android.view.*
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.adapter.ReservationDetailsAdapter
import it.polito.mad.g26.playingcourtreservation.model.Service
import it.polito.mad.g26.playingcourtreservation.model.custom.ServiceWithFee
import it.polito.mad.g26.playingcourtreservation.ui.CustomTextView
import it.polito.mad.g26.playingcourtreservation.util.makeInvisible
import it.polito.mad.g26.playingcourtreservation.util.makeVisible
import it.polito.mad.g26.playingcourtreservation.util.setupActionBar
import it.polito.mad.g26.playingcourtreservation.viewmodel.ReservationWithDetailsVM


class ReservationDetailsFragment : Fragment(R.layout.fragment_reservation_details) {

    private val args: ReservationDetailsFragmentArgs by navArgs()
    private val reservationWithDetailsVM by viewModels<ReservationWithDetailsVM>()
    private lateinit var servicesAll: List<ServiceWithFee>
    private lateinit var servicesUsed: List<Service>
    private lateinit var servicesChosen: List<ServiceWithFee>
    private val dummyService = Service()
    private val dummyServiceWithFee = ServiceWithFee(dummyService, 0.0f)
    private val dummyListServiceWithFee = listOf(dummyServiceWithFee)
    private val today = Calendar.getInstance()
    private lateinit var reservationReviewMCV: MaterialCardView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBar(activity, "Reservation Details", true)

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

        // Retrieve Reservation Details
        val reservationId = args.reservationId
        reservationReviewMCV = view.findViewById(R.id.reservationReviewMCV)

        reservationWithDetailsVM
            .getReservationWithDetailsById(reservationId)
            .observe(viewLifecycleOwner) { reservation ->
                servicesUsed = reservation.services
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
                    set(Calendar.HOUR_OF_DAY, hourRes-1)
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
                val viewReservationButtons = view.findViewById<ConstraintLayout>(R.id.reservation_buttons)

                //Show reservation buttons if future or review button if past
                if (today <= calendarRes) {
                    // Inflate the new layout with two buttons of reservation
                    val inflater = LayoutInflater.from(requireContext())
                    val viewDeleteAndEdit = inflater.inflate(R.layout.delete_and_edit_buttons, viewReservationButtons, false)
                    val deleteButton = viewDeleteAndEdit.findViewById<MaterialButton>(R.id.delete_reservation_button)
                    val editButton = viewDeleteAndEdit.findViewById<MaterialButton>(R.id.modify_reservation_button)
                    viewReservationButtons.addView(viewDeleteAndEdit)

                    deleteButton.setOnClickListener {
                        builder.show()
                    }
                    editButton.setOnClickListener {
                        val action =
                            ReservationDetailsFragmentDirections.openReservationEdit(reservationId)
                        findNavController().navigate(action)
                    }

                }else{
                    //Inflate with button of review
                    reservationWithDetailsVM.findReservationReview(reservationId, 1).observe(viewLifecycleOwner){review->
                        if (review == null){
                            reservationReviewMCV.makeInvisible()
                            viewReservationButtons.removeAllViews()
                            val inflater = LayoutInflater.from(requireContext())
                            val viewAddReview = inflater.inflate(R.layout.add_review_button, viewReservationButtons, false)
                            val reviewAddButton= viewAddReview.findViewById<MaterialButton>(R.id.add_review_button)
                            viewReservationButtons.addView(viewAddReview)
                            reviewAddButton.setOnClickListener {
                                val addReviewDialog = CustomDialogAlertAddReview.newInstance(reservationId, 1)
                                addReviewDialog.show(parentFragmentManager, CustomDialogAlertAddReview.TAG)
                            }
                        }else{
                            viewReservationButtons.removeAllViews()
                            val inflater = LayoutInflater.from(requireContext())
                            val viewEditDeleteReview = inflater.inflate(R.layout.delete_and_edit_review_buttons, viewReservationButtons, false)
                            val reviewEditButton= viewEditDeleteReview.findViewById<MaterialButton>(R.id.modify_review_button)
                            val reviewDeleteButton= viewEditDeleteReview.findViewById<MaterialButton>(R.id.delete_review_button)
                            viewReservationButtons.addView(viewEditDeleteReview)
                            reservationReviewMCV.makeVisible()
                            val rating = view.findViewById<RatingBar>(R.id.rating)
                            val reviewDate = view.findViewById<TextView>(R.id.reviewDateTV)
                            val reviewText = view.findViewById<TextView>(R.id.reviewTextTV)
                            rating.rating = review.rating
                            reviewDate.text = review.date
                            reviewText.text = review.text
                            reviewDeleteButton.setOnClickListener {
                                //Alert Dialog
                                val builderDeleteReview = AlertDialog.Builder(requireContext(), R.style.MyAlertDialogStyle)
                                builderDeleteReview.setMessage("Are you sure you want to delete the review?")
                                builderDeleteReview.setPositiveButton("Yes") { _, _ ->
                                    // User clicked OK button
                                    reservationWithDetailsVM.deleteReviewById(reservationId, 1)
                                }
                                builderDeleteReview.setNegativeButton("No") { _, _ ->
                                    // User cancelled the dialog
                                }
                                builderDeleteReview.show()
                            }
                            reviewEditButton.setOnClickListener {
                                val addReviewDialog = CustomDialogAlertAddReview.newInstance(reservationId, 1)
                                addReviewDialog.show(parentFragmentManager, CustomDialogAlertAddReview.TAG)
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
                                    recyclerView.adapter =
                                        ReservationDetailsAdapter(dummyListServiceWithFee, true)
                                else
                                    recyclerView.adapter = ReservationDetailsAdapter(servicesChosen, false)
                            }
                    }
            }


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
}