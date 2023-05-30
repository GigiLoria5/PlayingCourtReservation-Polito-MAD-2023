package it.polito.mad.g26.playingcourtreservation.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.RatingBar
import android.widget.TextView
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
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.adapter.searchCourtAdapters.ServiceWithFeeAdapter
import it.polito.mad.g26.playingcourtreservation.util.HorizontalSpaceItemDecoration
import it.polito.mad.g26.playingcourtreservation.util.UiState
import it.polito.mad.g26.playingcourtreservation.util.hideActionBar
import it.polito.mad.g26.playingcourtreservation.util.makeInvisible
import it.polito.mad.g26.playingcourtreservation.util.makeVisible
import it.polito.mad.g26.playingcourtreservation.util.toast
import it.polito.mad.g26.playingcourtreservation.viewmodel.ReservationDetailsViewModel

@AndroidEntryPoint
class ReservationDetailsFragment : Fragment(R.layout.reservation_details_fragment) {

    private val args: ReservationDetailsFragmentArgs by navArgs()
    private val viewModel by viewModels<ReservationDetailsViewModel>()

    // Visual Components
    private lateinit var reservationReviewMCV: MaterialCardView
    private lateinit var sportCenterPhoneNumberMCV: MaterialCardView
    private lateinit var viewReservationButtons: ConstraintLayout // View to be inflated with buttons

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // VM Initialization
        val reservationId = args.reservationId
        viewModel.initialize(reservationId)

        // Setup late init variables and visual components
        reservationReviewMCV = view.findViewById(R.id.reservationReviewMCV)
        sportCenterPhoneNumberMCV = view.findViewById(R.id.sportCenterPhoneNumberMCV)
        viewReservationButtons = view.findViewById(R.id.reservation_buttons)
        val centerName = view.findViewById<TextView>(R.id.sportCenter_name)
        val centerTime = view.findViewById<TextView>(R.id.sportCenter_time)
        val field = view.findViewById<TextView>(R.id.court_name)
        val sport = view.findViewById<TextView>(R.id.sport)
        val address = view.findViewById<TextView>(R.id.address)
        val price = view.findViewById<TextView>(R.id.price)
        val date = view.findViewById<TextView>(R.id.date)
        val serviceTitle = view.findViewById<TextView>(R.id.service_title)
        val serviceRV = view.findViewById<RecyclerView>(R.id.service_list)

        /* CUSTOM TOOLBAR MANAGEMENT*/
        val customToolBar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.customToolBar)
        customToolBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        /*BACK BUTTON MANAGEMENT*/
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        // Alert Dialog
        val builder = AlertDialog.Builder(requireContext(), R.style.MyAlertDialogStyle)
        builder.setMessage("Are you sure you want to delete the reservation?")
        builder.setPositiveButton("Confirm") { _, _ ->
            viewModel.deleteReservation()
            findNavController().popBackStack()
        }
        builder.setNegativeButton("Cancel") { _, _ ->
        }

        /*BACK BUTTON MANAGEMENT*/
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        // Retrieve Reservation Details
        viewModel.getReservationAndAllSportCenterServices()
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
                    // TODO: Stop Animation
                    // Update UI with Reservation Details
                    val reservation = viewModel.reservation
                    val reservationSportCenter = viewModel.sportCenter
                    val reservationCourt = reservationSportCenter.courts
                        .filter { it.id == reservation.courtId }[0]
                    val reservationServicesWithFee = reservationSportCenter.services
                        .filter { reservation.services.contains(it.name) }
                    if (reservationServicesWithFee.isEmpty())
                        serviceTitle.text =
                            getString(R.string.reservation_no_services_chosen)
                    else {
                        val adapter = ServiceWithFeeAdapter(
                            reservationServicesWithFee,
                            isServiceNameInList = { false },
                            isClickable = false
                        )
                        val itemDecoration =
                            HorizontalSpaceItemDecoration(
                                resources.getDimensionPixelSize(
                                    R.dimen.chip_distance
                                )
                            )
                        serviceRV.addItemDecoration(itemDecoration)
                        serviceRV.adapter = adapter
                    }
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
                    // Show reservation buttons if future or review button is past
                    if (viewModel.nowIsBeforeReservationDateTime()) {
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
                                ReservationDetailsFragmentDirections.openReservationEdit(
                                    1 // TODO: pass correct value
                                )
                            findNavController().navigate(action)
                        }
                        return@observe
                    }
                    loadReview()
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

    private fun loadReview() {
        val review = viewModel.review
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
                    CustomDialogAlertAddReview.newInstance("1", "1") // TODO: fix this
                addReviewDialog.show(
                    parentFragmentManager,
                    CustomDialogAlertAddReview.TAG
                )
            }
            return
        }
        // Review found
        viewReservationButtons.removeAllViews()
        val reviewEditButton =
            requireView().findViewById<MaterialButton>(R.id.modifyReviewButton)
        val reviewDeleteButton =
            requireView().findViewById<MaterialButton>(R.id.deleteReviewButton)
        reservationReviewMCV.makeVisible()
        val rating = requireView().findViewById<RatingBar>(R.id.rating)
        val reviewDate = requireView().findViewById<TextView>(R.id.reviewDateTV)
        val reviewText = requireView().findViewById<TextView>(R.id.reviewTextTV)
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
            builderDeleteReview.setPositiveButton("Confirm") { _, _ ->
                // User clicked OK button
                viewModel.deleteUserReview()
                toast("Review deleted successfully")
            }
            builderDeleteReview.setNegativeButton("Cancel") { _, _ ->
                // User cancelled the dialog
            }
            builderDeleteReview.show()
        }
        reviewEditButton.setOnClickListener {
            val addReviewDialog =
                CustomDialogAlertAddReview.newInstance("1", "1") // TODO: fix this
            addReviewDialog.show(
                parentFragmentManager,
                CustomDialogAlertAddReview.TAG
            )
        }
    }

    override fun onResume() {
        super.onResume()
        hideActionBar(activity)
    }
}