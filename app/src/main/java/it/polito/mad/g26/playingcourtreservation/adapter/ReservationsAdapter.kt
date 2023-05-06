package it.polito.mad.g26.playingcourtreservation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.model.Reservation

class ReservationsAdapter : RecyclerView.Adapter<ReservationsAdapter.ReservationsViewHolder>() {

    private val reservations = mutableListOf<Reservation>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationsViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.reservation_item_view, parent, false)
        return ReservationsViewHolder(v)
    }

    override fun onBindViewHolder(holder: ReservationsViewHolder, position: Int) {
        holder.bind(reservations[position])
    }

    override fun getItemCount(): Int = reservations.size

    fun updateData(reservations: List<Reservation>) {
        this.reservations.clear()
        this.reservations.addAll(reservations)
    }

    inner class ReservationsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val timeView = view.findViewById<TextView>(R.id.itemReservationTimeText)
        private val sportCenterNameView =
            view.findViewById<TextView>(R.id.itemReservationSportCenterNameText)
        private val sportCenterAddressView =
            view.findViewById<TextView>(R.id.itemReservationSportCenterAddressText)
        private val sportCenterFieldSportView =
            view.findViewById<TextView>(R.id.itemReservationSportCenterFieldSportText)

        fun bind(reservation: Reservation) {
            timeView.text = reservation.time
            sportCenterNameView.text = "Sport Center"
            sportCenterAddressView.text = "Address, City"
            sportCenterFieldSportView.text = "Field Name, Sport"
            super.itemView.setOnClickListener { println("Reservation ${reservation.id}") }
        }
    }
}