package it.polito.mad.g26.playingcourtreservation.fragment

import android.content.Intent
import android.net.Uri
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.adapter.ReservationDetailsAdapter
import it.polito.mad.g26.playingcourtreservation.adapter.searchCourtAdapters.ServiceWithFeeAdapter
import it.polito.mad.g26.playingcourtreservation.model.Court
import it.polito.mad.g26.playingcourtreservation.model.User
import it.polito.mad.g26.playingcourtreservation.util.HorizontalSpaceItemDecoration
import it.polito.mad.g26.playingcourtreservation.util.UiState
import it.polito.mad.g26.playingcourtreservation.util.hideActionBar
import it.polito.mad.g26.playingcourtreservation.util.makeGone
import it.polito.mad.g26.playingcourtreservation.util.makeInvisible
import it.polito.mad.g26.playingcourtreservation.util.makeVisible
import it.polito.mad.g26.playingcourtreservation.util.startShimmerMCVAnimation
import it.polito.mad.g26.playingcourtreservation.util.startShimmerRVAnimation
import it.polito.mad.g26.playingcourtreservation.util.startShimmerTextAnimation
import it.polito.mad.g26.playingcourtreservation.util.stopShimmerMCVAnimation
import it.polito.mad.g26.playingcourtreservation.util.stopShimmerRVAnimation
import it.polito.mad.g26.playingcourtreservation.util.stopShimmerTextAnimation
import it.polito.mad.g26.playingcourtreservation.util.timestampToDate
import it.polito.mad.g26.playingcourtreservation.util.toast
import it.polito.mad.g26.playingcourtreservation.viewmodel.ReservationDetailsViewModel
import it.polito.mad.g26.playingcourtreservation.viewmodel.SharedReservationDetailsViewModel

@AndroidEntryPoint
class ReservationDetailsFragment : Fragment(R.layout.reservation_details_fragment) {

    private val args: ReservationDetailsFragmentArgs by navArgs()
    private val viewModel by viewModels<ReservationDetailsViewModel>()
    private lateinit var sharedReservationDetailsViewModel: SharedReservationDetailsViewModel

    // Visual Components
    private lateinit var reservationReviewMCV: MaterialCardView
    private lateinit var sportCenterPhoneNumberMCV: MaterialCardView
    private lateinit var viewReservationButtons: ConstraintLayout // View to be inflated with buttons
    private lateinit var shimmerFrameLayoutRV: ShimmerFrameLayout
    private lateinit var rowTitleRVShimmer: ShimmerFrameLayout
    private lateinit var shimmerInviteB: ShimmerFrameLayout
    private lateinit var shimmerTopBar: ShimmerFrameLayout
    private lateinit var shimmerTopBarImage: ShimmerFrameLayout
    private lateinit var participantsTitle: TextView
    private lateinit var participantsRecyclerView: RecyclerView
    private lateinit var inviteButtonMCV: MaterialCardView
    private lateinit var topBarMCV: MaterialCardView
    private lateinit var shimmerServiceRV: ShimmerFrameLayout
    private lateinit var serviceRV: RecyclerView

    // Action is performing
    private var reviewDeleteInProgress = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val reservationId = args.reservationId
        sharedReservationDetailsViewModel =
            ViewModelProvider(requireActivity())[SharedReservationDetailsViewModel::class.java]

        // VM Initialization
        val navController = findNavController()
        val navigationArg =
            navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>("key")
        navigationArg?.observe(viewLifecycleOwner) {
            viewModel.initialize(it)
            viewModel.loadReservationAndSportCenterInformation()
        }
        if (navigationArg?.value == null) {
            viewModel.initialize(reservationId)
            viewModel.loadReservationAndSportCenterInformation()
        }

        // Setup late init variables and visual components
        shimmerServiceRV = view.findViewById(R.id.shimmerServicesRV)
        shimmerTopBarImage = view.findViewById(R.id.shimmerImageMCV)
        shimmerTopBar = view.findViewById(R.id.shimmerTopBar)
        shimmerInviteB = view.findViewById(R.id.shimmerParticipantButton)
        rowTitleRVShimmer = view.findViewById(R.id.shimmerParticipantTitle)
        shimmerFrameLayoutRV = view.findViewById(R.id.shimmerViewRV)
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
        serviceRV = view.findViewById(R.id.service_list)
        participantsRecyclerView = view.findViewById(R.id.player_list)
        val requesterRecyclerView = view.findViewById<RecyclerView>(R.id.requester_list)
        val requestersLayout = view.findViewById<ConstraintLayout>(R.id.requester_layout)
        participantsTitle = view.findViewById(R.id.player_title)
        val inviteButton = view.findViewById<MaterialButton>(R.id.search_players_button)
        inviteButtonMCV = view.findViewById(R.id.inviteButtonMCVForShimmer)
        topBarMCV = view.findViewById(R.id.sportCenterDataMCVForShimmer)

        /* CUSTOM TOOLBAR MANAGEMENT*/
        val customToolBar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.customToolBar)
        customToolBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        /*BACK BUTTON MANAGEMENT*/
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        // Delete Alert Dialog
        val builder = AlertDialog.Builder(requireContext(), R.style.MyAlertDialogStyle)
        builder.setMessage("Are you sure you want to delete the reservation?")
        builder.setPositiveButton("Confirm") { _, _ ->
            viewModel.deleteReservation()
        }
        builder.setNegativeButton("Cancel") { _, _ ->
        }
        handleDeleteReservation()

        // Remove participant Alert Dialog
        val participantRemoveAD = AlertDialog.Builder(requireContext(), R.style.MyAlertDialogStyle)
        participantRemoveAD.setMessage("Are you sure you want to be removed from the reservation?")
        participantRemoveAD.setPositiveButton("Confirm") { _, _ ->
            viewModel.removeParticipant()
        }
        participantRemoveAD.setNegativeButton("Cancel") { _, _ ->
        }

        /*BACK BUTTON MANAGEMENT*/
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        // Retrieve Reservation Details
        viewModel.loadingState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    shimmerFrameLayoutRV.makeVisible()
                    shimmerFrameLayoutRV.startShimmerRVAnimation(participantsRecyclerView)
                    shimmerServiceRV.startShimmerRVAnimation(serviceRV)
                    rowTitleRVShimmer.startShimmerTextAnimation(participantsTitle)
                    shimmerInviteB.startShimmerMCVAnimation(inviteButtonMCV)
                    shimmerTopBar.startShimmerMCVAnimation(topBarMCV)
                    shimmerTopBarImage.startShimmerMCVAnimation(sportCenterPhoneNumberMCV)
                }

                is UiState.Failure -> {
                    shimmerFrameLayoutRV.stopShimmerRVAnimation(participantsRecyclerView)
                    rowTitleRVShimmer.stopShimmerTextAnimation(participantsTitle)
                    shimmerInviteB.stopShimmerMCVAnimation(inviteButtonMCV)
                    shimmerTopBar.stopShimmerMCVAnimation(topBarMCV)
                    shimmerTopBarImage.stopShimmerMCVAnimation(sportCenterPhoneNumberMCV)
                    shimmerServiceRV.stopShimmerRVAnimation(serviceRV)
                    shimmerFrameLayoutRV.makeGone()
                    toast(state.error ?: "Unable to get reservation details")
                }

                is UiState.Success -> {
                    shimmerFrameLayoutRV.stopShimmerRVAnimation(participantsRecyclerView)
                    rowTitleRVShimmer.stopShimmerTextAnimation(participantsTitle)
                    shimmerInviteB.stopShimmerMCVAnimation(inviteButtonMCV)
                    shimmerTopBar.stopShimmerMCVAnimation(topBarMCV)
                    shimmerTopBarImage.stopShimmerMCVAnimation(sportCenterPhoneNumberMCV)
                    shimmerServiceRV.stopShimmerRVAnimation(serviceRV)
                    shimmerFrameLayoutRV.makeGone()
                    if (reviewDeleteInProgress) { // It means the delete was successful
                        reviewDeleteInProgress = false
                        toast("Review deleted successfully")
                    }
                    // Update UI with Reservation Details
                    val reservation = viewModel.reservation
                    sharedReservationDetailsViewModel.reservation = reservation
                    val reservationSportCenter = viewModel.sportCenter
                    sharedReservationDetailsViewModel.reservationSportCenter =
                        reservationSportCenter
                    val reservationCourt = reservationSportCenter.courts
                        .filter { it.id == reservation.courtId }[0]
                    sharedReservationDetailsViewModel.reservationCourt = reservationCourt
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
                    //Telephone icon
                    sportCenterPhoneNumberMCV.setOnClickListener {
                        val intent = Intent(Intent.ACTION_DIAL)
                        intent.data = Uri.parse("tel:${reservationSportCenter.phoneNumber}")
                        startActivity(intent)
                    }
                    //Show applicant confirmed
                    val currentUser = viewModel.currentUser
                    val creatorUser = viewModel.creatorUser
                    val participants = listOf(creatorUser) + viewModel.participants
                    val participantsAdapter =
                        ReservationDetailsAdapter(
                            participants, 1, viewModel.court.sport,
                            { userId ->
                                val direction =
                                    ReservationDetailsFragmentDirections.openShowProfile(userId)
                                findNavController().navigate(direction)
                            },
                            viewModel.userPicturesMap,
                            {},
                            {}
                        )
                    participantsRecyclerView.adapter = participantsAdapter
                    participantsRecyclerView.layoutManager = GridLayoutManager(context, 2)
                    val maxParticipants = Court.getSportTotParticipants(viewModel.court.sport)
                    participantsTitle.text = view.context.getString(
                        R.string.participants_concatenate_title,
                        participants.size.toString(),
                        maxParticipants.toString()
                    )

                    // Show reservation buttons+ applicants list if future or review button is past
                    if (viewModel.nowIsBeforeReservationDateTime()) {

                        //CREATOR
                        if (reservation.userId == viewModel.userId) {
                            //Show invite button+requester list if there is space available
                            if (participants.size < maxParticipants) {
                                inviteButton.makeVisible()
                                inviteButton.setOnClickListener {
                                    val direction = ReservationDetailsFragmentDirections
                                        .actionReservationDetailsFragmentToInviteUsersFragment(
                                            reservation.id, reservation.date, reservation.time,
                                            reservationSportCenter.city, reservationCourt.sport
                                        )
                                    findNavController().navigate(direction)
                                }
                                //Show requesters list if not empty
                                if (reservation.requests.isNotEmpty()) {
                                    requestersLayout.makeVisible()
                                    val requesterAdapter =
                                        ReservationDetailsAdapter(
                                            viewModel.requesters,
                                            2,
                                            viewModel.court.sport,
                                            { userId ->
                                                val direction =
                                                    ReservationDetailsFragmentDirections
                                                        .openShowProfile(userId)
                                                findNavController().navigate(direction)
                                            },
                                            viewModel.userPicturesMap,
                                            { requesterToParticipant ->
                                                showConfirmationDialog(
                                                    requesterToParticipant, 1
                                                )
                                            },
                                            { requesterToUser ->
                                                showConfirmationDialog(
                                                    requesterToUser, 2
                                                )
                                            }
                                        )
                                    requesterRecyclerView.adapter = requesterAdapter
                                    requesterRecyclerView.layoutManager =
                                        LinearLayoutManager(context)
                                } else
                                    requestersLayout.makeGone()
                            } else {
                                if (reservation.requests.isNotEmpty() || reservation.invitees.isNotEmpty())
                                    viewModel.removeAllInviteesAndRequesters()
                            }
                            // Inflate button to edit/delete reservation
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
                                    )
                                findNavController().navigate(action)
                            }

                        } else if (reservation.participants.contains(currentUser.id)) {
                            //PARTICIPANT-> button to remove itself and send notification
                            viewReservationButtons.removeAllViews()
                            val inflater = LayoutInflater.from(requireContext())
                            val viewRemoveFromReservation = inflater.inflate(
                                R.layout.reservation_details_participant_button,
                                viewReservationButtons,
                                false
                            )
                            viewReservationButtons.addView(viewRemoveFromReservation)
                            val removeButton =
                                viewRemoveFromReservation.findViewById<MaterialButton>(R.id.reservation_details_remove_button)
                            removeButton.setOnClickListener {
                                participantRemoveAD.show()
                            }
                        } else if (reservation.requests.contains(currentUser.id)) {
                            //REQUESTER-> button not clickable already sent invite
                            viewReservationButtons.removeAllViews()
                            val inflater = LayoutInflater.from(requireContext())
                            val viewRequest = inflater.inflate(
                                R.layout.reservation_details_requester_button,
                                viewReservationButtons,
                                false
                            )
                            viewReservationButtons.addView(viewRequest)
                        } else if (reservation.invitees.contains(currentUser.id)) {
                            //INVITEES(invited by creator)-> button accept or reject
                            viewReservationButtons.removeAllViews()
                            val inflater = LayoutInflater.from(requireContext())
                            val viewAcceptOrReject = inflater.inflate(
                                R.layout.reservation_details_invited_buttons,
                                viewReservationButtons,
                                false
                            )
                            viewReservationButtons.addView(viewAcceptOrReject)
                            val acceptButton =
                                viewAcceptOrReject.findViewById<MaterialButton>(R.id.reservation_details_accept_button)
                            acceptButton.setOnClickListener {
                                viewModel.addParticipantAndRemoveInvitee(currentUser.id) { message ->
                                    toast(
                                        message
                                    )
                                }
                            }
                            val rejectButton =
                                viewAcceptOrReject.findViewById<MaterialButton>(R.id.reservation_details_reject_button)
                            rejectButton.setOnClickListener {
                                viewModel.removeInvitee(currentUser.id)
                            }
                        } else {
                            //USER-> button to ask to join and become requester
                            viewReservationButtons.removeAllViews()
                            if (participants.size < maxParticipants) {
                                val inflater = LayoutInflater.from(requireContext())
                                val viewAsk = inflater.inflate(
                                    R.layout.reservation_details_user_button,
                                    viewReservationButtons,
                                    false
                                )
                                val askButton =
                                    viewAsk.findViewById<MaterialButton>(R.id.reservation_details_ask_button)
                                viewReservationButtons.addView(viewAsk)
                                askButton.setOnClickListener {
                                    viewModel.addRequester(currentUser.id)
                                }
                            } else {
                                val inflater = LayoutInflater.from(requireContext())
                                val viewFull = inflater.inflate(
                                    R.layout.reservation_details_requester_button,
                                    viewReservationButtons,
                                    false
                                )
                                viewReservationButtons.addView(viewFull)
                                val removeButton =
                                    viewFull.findViewById<MaterialButton>(R.id.reservation_details_ask_button)
                                removeButton.text = getString(R.string.match_already_full)
                            }
                        }
                        return@observe
                    }
                    if ((reservation.userId == viewModel.userId
                                || reservation.participants.contains(viewModel.userId))
                        && participants.size == maxParticipants
                    )
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

    private fun handleDeleteReservation() {
        viewModel.deleteState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    shimmerFrameLayoutRV.makeVisible()
                    shimmerFrameLayoutRV.startShimmerRVAnimation(participantsRecyclerView)
                    shimmerServiceRV.startShimmerRVAnimation(serviceRV)
                    rowTitleRVShimmer.startShimmerTextAnimation(participantsTitle)
                    shimmerInviteB.startShimmerMCVAnimation(inviteButtonMCV)
                    shimmerTopBar.startShimmerMCVAnimation(topBarMCV)
                    shimmerTopBarImage.startShimmerMCVAnimation(sportCenterPhoneNumberMCV)
                }

                is UiState.Failure -> {
                    shimmerFrameLayoutRV.stopShimmerRVAnimation(participantsRecyclerView)
                    rowTitleRVShimmer.stopShimmerTextAnimation(participantsTitle)
                    shimmerInviteB.stopShimmerMCVAnimation(inviteButtonMCV)
                    shimmerTopBar.stopShimmerMCVAnimation(topBarMCV)
                    shimmerTopBarImage.stopShimmerMCVAnimation(sportCenterPhoneNumberMCV)
                    shimmerServiceRV.stopShimmerRVAnimation(serviceRV)
                    shimmerFrameLayoutRV.makeGone()
                    toast(state.error ?: "Unable to delete reservation")
                }

                is UiState.Success -> {
                    shimmerFrameLayoutRV.stopShimmerRVAnimation(participantsRecyclerView)
                    rowTitleRVShimmer.stopShimmerTextAnimation(participantsTitle)
                    shimmerInviteB.stopShimmerMCVAnimation(inviteButtonMCV)
                    shimmerTopBar.stopShimmerMCVAnimation(topBarMCV)
                    shimmerTopBarImage.stopShimmerMCVAnimation(sportCenterPhoneNumberMCV)
                    shimmerServiceRV.stopShimmerRVAnimation(serviceRV)
                    shimmerFrameLayoutRV.makeGone()
                    findNavController().popBackStack()
                    toast("Reservation was successfully deleted")
                }
            }
        }
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
            val reviewAddButton = viewAddReview
                .findViewById<MaterialButton>(R.id.add_review_button)
            viewReservationButtons.addView(viewAddReview)
            reviewAddButton.setOnClickListener {
                val addReviewDialog =
                    AddReviewDialogFragment.newInstance(
                        viewModel.reservationId,
                        viewModel.userId
                    ) { viewModel.loadReservationAndSportCenterInformation() }
                addReviewDialog.show(
                    parentFragmentManager,
                    AddReviewDialogFragment.TAG
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
        reviewDate.text = timestampToDate(review.timestamp)
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
                reviewDeleteInProgress = true
            }
            builderDeleteReview.setNegativeButton("Cancel") { _, _ ->
                // User cancelled the dialog
            }
            builderDeleteReview.show()
        }
        reviewEditButton.setOnClickListener {
            val addReviewDialog =
                AddReviewDialogFragment.newInstance(
                    viewModel.reservationId,
                    viewModel.userId
                ) { viewModel.loadReservationAndSportCenterInformation() }
            addReviewDialog.show(
                parentFragmentManager,
                AddReviewDialogFragment.TAG
            )
        }
    }

    private fun showConfirmationDialog(user: User, mode: Int) {
        //Requester being accepted
        if (mode == 1) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Accept the user")
                .setMessage("Confirm to add ${user.username}?")
                .setPositiveButton("Confirm") { dialog, _ ->
                    dialog.dismiss()
                    viewModel.addParticipantAndDeleteRequester(user.id) { message ->
                        toast(message)
                        viewModel.loadReservationAndSportCenterInformation()
                    }
                }.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
            //Requester being rejected
        } else {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Reject the user")
                .setMessage("Confirm to reject ${user.username}?")
                .setPositiveButton("Confirm") { dialog, _ ->
                    dialog.dismiss()
                    viewModel.deleteRequester(user.id)
                }.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

    }

    override fun onResume() {
        super.onResume()
        hideActionBar(activity)
    }
}