package it.polito.mad.g26.playingcourtreservation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.fragment.ReservationsFragmentDirections
import it.polito.mad.g26.playingcourtreservation.model.ReservationWithDetails
import it.polito.mad.g26.playingcourtreservation.model.toSportColor
import it.polito.mad.g26.playingcourtreservation.util.getColorCompat

class ReservationsAdapter : RecyclerView.Adapter<ReservationsAdapter.ReservationsViewHolder>() {

    private val reservations = mutableListOf<ReservationWithDetails>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationsViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.reservations_reservation_item_view, parent, false)
        return ReservationsViewHolder(v)
    }

    override fun onBindViewHolder(holder: ReservationsViewHolder, position: Int) {
        holder.bind(reservations[position])
    }

    override fun getItemCount(): Int = reservations.size

    fun updateData(reservations: List<ReservationWithDetails>) {
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
        private val reservationAmount = view.findViewById<TextView>(R.id.itemReservationAmountText)

        fun bind(reservationWithDetails: ReservationWithDetails) {
            val reservation = reservationWithDetails.reservation
            val sportCenter = reservationWithDetails.courtWithDetails.sportCenter
            val court = reservationWithDetails.courtWithDetails.court
            val sport = reservationWithDetails.courtWithDetails.sport
            timeView.apply {
                text = reservation.time
                setBackgroundColor(itemView.context.getColorCompat(sport.toSportColor()))
            }
            sportCenterNameView.text = sportCenter.name
            sportCenterAddressView.text = super.itemView.context.getString(
                R.string.reservation_info_concatenation,
                sportCenter.address,
                sportCenter.city
            )
            sportCenterFieldSportView.text = super.itemView.context.getString(
                R.string.reservation_info_concatenation,
                court.name,
                sport.name
            )
            reservationAmount.text = super.itemView.context.getString(
                R.string.reservation_info_amount,
                String.format("%.1f", reservation.amount)
            )

            super.itemView.setOnClickListener {
                // TODO: send right value
                val action = ReservationsFragmentDirections
                    .openReservationDetails("")
                super.itemView.findNavController().navigate(action)
            }
        }
    }
}