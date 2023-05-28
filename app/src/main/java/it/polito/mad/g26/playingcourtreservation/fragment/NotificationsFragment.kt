package it.polito.mad.g26.playingcourtreservation.fragment

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.adapter.NotificationsAdapter
import it.polito.mad.g26.playingcourtreservation.model.Notification
import it.polito.mad.g26.playingcourtreservation.util.hideActionBar
import it.polito.mad.g26.playingcourtreservation.util.makeInvisible
import it.polito.mad.g26.playingcourtreservation.util.makeVisible

class NotificationsFragment : Fragment(R.layout.notification_fragment) {

    private lateinit var customToolBar: Toolbar
    private lateinit var notificationsRV: RecyclerView
    private lateinit var noNotificationMCV: MaterialCardView
    private lateinit var deleteAllNotificationMCV: MaterialCardView
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
        var notifications = listOf<Notification>()

        noNotificationMCV = view.findViewById(R.id.noNotificationsFoundMCV)
        deleteAllNotificationMCV = view.findViewById(R.id.deleteNotificationsMCV)

        /*Set-up recycle view */
        notificationsRV = view.findViewById(R.id.notificationsRV)

        var n0 = Notification()
        n0.id = 0
        n0.isRead = true
        n0.timestamp = "28-05-2023 18:32:05"
        n0.message = "Filippo invited you to play"
        n0.idReservation = 1

        var n1 = Notification()
        n1.id = 1
        n1.isRead = false
        n1.timestamp = "28-05-2023 18:32:20"
        n1.message = "Marco invited you to play"
        n1.idReservation = 1

        var n2 = Notification()
        n2.id = 2
        n2.isRead = false
        n2.timestamp = "28-05-2023 18:32:40"
        n2.message = "Luca invited you to play"
        n2.idReservation = 2

        notifications += n0
        notifications += n1
        notifications += n2

        notificationsRV.adapter = NotificationsAdapter(notifications)
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
    }

    override fun onResume() {
        super.onResume()
        hideActionBar(activity)
    }
}