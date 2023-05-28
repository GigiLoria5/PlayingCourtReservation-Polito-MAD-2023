package it.polito.mad.g26.playingcourtreservation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.model.Notification

class NotificationsAdapter(
    private var notifications: MutableList<Notification>
) : RecyclerView.Adapter<NotificationsAdapter.NotificationsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(viewType, parent, false)
        return NotificationsViewHolder(v)
    }

    override fun onBindViewHolder(holder: NotificationsViewHolder, position: Int) {
        holder.bind(notifications[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateNotifications(updatedCollection: MutableList<Notification>) {
        this.notifications = updatedCollection
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = notifications.size

    override fun getItemViewType(position: Int): Int {
        if (notifications[position].isRead){
            return R.layout.notification_item_isread
        }
        return R.layout.notification_item
    }

    fun removeItem(position: Int): Notification {
        val deleteNotification = notifications[position]
        notifications.removeAt(position)
        notifyItemRemoved(position)
        return deleteNotification
    }

    inner class NotificationsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val timestamp = view.findViewById<TextView>(R.id.notificationTimestampTV)
        private val message = view.findViewById<TextView>(R.id.notificationMessageTV)

        fun bind(notification: Notification) {
            timestamp.text = notification.timestamp
            message.text = notification.message
        }
    }
}