package it.polito.mad.g26.playingcourtreservation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.model.Notification
import it.polito.mad.g26.playingcourtreservation.util.timestampToDate

class NotificationsAdapter(
    private var notifications: List<Notification>
) : RecyclerView.Adapter<NotificationsAdapter.NotificationsViewHolder>() {

    private lateinit var mListener: OnItemClickListener
    fun interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(listener: OnItemClickListener){
        mListener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(viewType, parent, false)
        return NotificationsViewHolder(v, mListener)
    }

    override fun onBindViewHolder(holder: NotificationsViewHolder, position: Int) {
        holder.bind(notifications[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateNotifications(updatedCollection: List<Notification>) {
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
        notifyItemRemoved(position)
        return deleteNotification
    }

    inner class NotificationsViewHolder(view: View, listener: OnItemClickListener) : RecyclerView.ViewHolder(view) {
        private val timestamp = view.findViewById<TextView>(R.id.notificationTimestampTV)
        private val message = view.findViewById<TextView>(R.id.notificationMessageTV)

        init {
            itemView.setOnClickListener{
                listener.onItemClick(absoluteAdapterPosition)
            }
        }
        fun bind(notification: Notification) {
            timestamp.text = timestampToDate(notification.timestamp)
            message.text = notification.message
        }
    }
}