package it.polito.mad.g26.playingcourtreservation.fragment

import android.graphics.Canvas
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.adapter.NotificationsAdapter
import it.polito.mad.g26.playingcourtreservation.model.Notification
import it.polito.mad.g26.playingcourtreservation.util.UiState
import it.polito.mad.g26.playingcourtreservation.util.hideActionBar
import it.polito.mad.g26.playingcourtreservation.util.makeInvisible
import it.polito.mad.g26.playingcourtreservation.util.makeVisible
import it.polito.mad.g26.playingcourtreservation.util.startShimmerAnimation
import it.polito.mad.g26.playingcourtreservation.util.stopShimmerAnimation
import it.polito.mad.g26.playingcourtreservation.util.toast
import it.polito.mad.g26.playingcourtreservation.viewmodel.NotificationsViewModel
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

@AndroidEntryPoint
class NotificationsFragment : Fragment(R.layout.notification_fragment) {

    private lateinit var customToolBar: Toolbar
    private lateinit var notificationsRV: RecyclerView
    private lateinit var noNotificationMCV: MaterialCardView
    private lateinit var deleteAllNotificationMCV: MaterialCardView
    private lateinit var notificationsAdapter: NotificationsAdapter
    private var deleteData: Notification? = null
    private val viewModel by viewModels<NotificationsViewModel>()
    private lateinit var notificationsShimmerView: ShimmerFrameLayout
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /* CUSTOM TOOLBAR MANAGEMENT*/
        customToolBar = view.findViewById(R.id.customToolBar)
        customToolBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        /*BACK BUTTON MANAGEMENT*/
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        noNotificationMCV = view.findViewById(R.id.noNotificationsFoundMCV)
        deleteAllNotificationMCV = view.findViewById(R.id.deleteNotificationsMCV)
        notificationsShimmerView = view.findViewById(R.id.notificationsShimmerView)

        /*Set-up recycle view */
        notificationsRV = view.findViewById(R.id.notificationsRV)
        notificationsAdapter = NotificationsAdapter(viewModel.notifications)
        notificationsRV.adapter = notificationsAdapter
        // Load the data needed
        viewModel.loadNotifications()
        viewModel.loadingState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    notificationsShimmerView.startShimmerAnimation(notificationsRV)
                }

                is UiState.Failure -> {
                    notificationsShimmerView.stopShimmerAnimation(notificationsRV)
                    toast(state.error ?: "Unable to get notifications")
                }

                is UiState.Success -> {
                    notificationsShimmerView.stopShimmerAnimation(notificationsRV)
                    // Update Notifications
                    val notifications = viewModel.notifications
                    if (notifications.isNotEmpty()) {
                        notificationsAdapter.updateNotifications(notifications)
                        noNotificationMCV.makeInvisible()
                        deleteAllNotificationMCV.makeVisible()
                    } else {
                        noNotificationMCV.makeVisible()
                        deleteAllNotificationMCV.makeInvisible()
                    }
                }
            }
        }
        // Dialog no reservation
        val builderNoReservation = AlertDialog.Builder(requireContext(), R.style.MyAlertDialogStyle)
        builderNoReservation.setMessage("The reservation associated with this notification is no longer available?")
        builderNoReservation.setPositiveButton("Ok") { _, _ ->
        }

        notificationsAdapter.setOnItemClickListener { position ->
            val reservationId = viewModel.notifications[position].reservationId
            viewModel.findReservation(reservationId)
            viewModel.loadingStateReservation.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is UiState.Loading -> {
                        // TODO: Add loading animation
                    }

                    is UiState.Failure -> {
                        // TODO: Stop loading animation
                        toast(state.error ?: "Reservation no longer available")
                    }

                    is UiState.Success -> {
                        // TODO: Stop loading animation
                        println("Success")
                        val action = NotificationsFragmentDirections
                            .actionNotificationFragmentToReservationDetailsFragment2(reservationId)
                        findNavController().navigate(action)
                    }
                }
            }
        }

        notificationsRV.addItemDecoration(DividerItemDecoration(this.activity, LinearLayout.VERTICAL))

        // Handle Menu Items
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_courts, menu)
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

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(notificationsRV)

        // Delete Alert Dialog
        val builder = AlertDialog.Builder(requireContext(), R.style.MyAlertDialogStyle)
        builder.setMessage("Are you sure you want to delete all notifications?")
        builder.setPositiveButton("Confirm") { _, _ ->
            viewModel.deleteAllCurrentUserNotifications()
        }
        builder.setNegativeButton("Cancel") { _, _ ->
        }

        deleteAllNotificationMCV.setOnClickListener {
            builder.show()
        }
    }

    private var simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.absoluteAdapterPosition
            when(direction){
                ItemTouchHelper.LEFT ->{
                    deleteData = notificationsAdapter.removeItem(position)
                    viewModel.deleteNotification(deleteData!!.id)
                    notificationsAdapter.updateNotifications(viewModel.notifications)
                    Snackbar.make(notificationsRV, "Notification deleted", Snackbar.LENGTH_LONG)
                        .setAction(
                            "Undo"
                        ) {
                            viewModel.addNotification(deleteData!!)
                            notificationsAdapter.updateNotifications(viewModel.notifications)
                        }.show()
                }
            }
        }
        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeLeftBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
                .addSwipeLeftActionIcon(android.R.drawable.ic_menu_delete)
                .addSwipeLeftLabel("Delete")
                .setSwipeLeftLabelColor(ContextCompat.getColor(requireContext(), R.color.white))
                .create()
                .decorate()
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

    }
    override fun onResume() {
        super.onResume()
        hideActionBar(activity)
    }
}