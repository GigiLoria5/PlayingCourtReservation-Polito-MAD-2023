package it.polito.mad.g26.playingcourtreservation.fragment

import android.graphics.Canvas
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.adapter.NotificationsAdapter
import it.polito.mad.g26.playingcourtreservation.model.Notification
import it.polito.mad.g26.playingcourtreservation.util.hideActionBar
import it.polito.mad.g26.playingcourtreservation.util.makeInvisible
import it.polito.mad.g26.playingcourtreservation.util.makeVisible
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator


class NotificationsFragment : Fragment(R.layout.notification_fragment) {

    private lateinit var customToolBar: Toolbar
    private lateinit var notificationsRV: RecyclerView
    private lateinit var noNotificationMCV: MaterialCardView
    private lateinit var deleteAllNotificationMCV: MaterialCardView
    private lateinit var notificationsAdapter: NotificationsAdapter
    private var deleteData: Notification? = null
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
        val notifications = mutableListOf<Notification>()

        noNotificationMCV = view.findViewById(R.id.noNotificationsFoundMCV)
        deleteAllNotificationMCV = view.findViewById(R.id.deleteNotificationsMCV)

        /*Set-up recycle view */
        notificationsRV = view.findViewById(R.id.notificationsRV)

        val n0 = Notification()
        n0.id = 0
        n0.isRead = true
        n0.timestamp = "28-05-2023 18:32:05"
        n0.message = "Filippo invited you to play"
        n0.idReservation = 1

        val n1 = Notification()
        n1.id = 1
        n1.isRead = false
        n1.timestamp = "28-05-2023 18:32:20"
        n1.message = "Marco invited you to play"
        n1.idReservation = 1

        val n2 = Notification()
        n2.id = 2
        n2.isRead = false
        n2.timestamp = "28-05-2023 18:32:40"
        n2.message = "Luca invited you to play"
        n2.idReservation = 20

        notifications += n0
        notifications += n1
        notifications += n2

        notificationsAdapter = NotificationsAdapter(notifications)
        notificationsRV.adapter = notificationsAdapter
        notificationsAdapter.setOnItemClickListener(object : NotificationsAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val action = NotificationsFragmentDirections
                    .actionNotificationFragmentToReservationDetailsFragment2(notifications[position].idReservation)
                findNavController().navigate(action)
            }

        })
        notificationsRV.addItemDecoration(DividerItemDecoration(this.activity, LinearLayout.VERTICAL))

        if (notifications.isNotEmpty()){
            noNotificationMCV.makeInvisible()
            deleteAllNotificationMCV.makeVisible()
        }else{
            noNotificationMCV.makeVisible()
            deleteAllNotificationMCV.makeInvisible()
        }

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