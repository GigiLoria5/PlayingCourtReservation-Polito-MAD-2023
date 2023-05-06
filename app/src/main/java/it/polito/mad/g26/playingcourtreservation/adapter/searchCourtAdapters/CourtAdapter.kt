package it.polito.mad.g26.playingcourtreservation.adapter.searchCourtAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.model.Court

class CourtAdapter(
    private val collection: List<Court>,
    private val isCourtReserved: (Int) -> Int //0=no, 1=from others, 2=from you

) :
    RecyclerView.Adapter<CourtAdapter.CourtViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourtViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.court_item, parent, false)
        return CourtViewHolder(view)
    }


    override fun onBindViewHolder(holder: CourtViewHolder, position: Int) {
        holder.bind(collection[position])
    }

    override fun onViewRecycled(holder: CourtViewHolder) {
        holder.unbind()
    }

    override fun getItemCount() = collection.size


    inner class CourtViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val courtName = itemView.findViewById<TextView>(R.id.courtNameTV)
        private val courtType = itemView.findViewById<TextView>(R.id.courtTypeTV)
        private val courtPrice = itemView.findViewById<TextView>(R.id.courtPriceTV)
        private val courtAvailability = itemView.findViewById<TextView>(R.id.courtAvailabilityTV)
        private val courtCardView = itemView.findViewById<CardView>(R.id.courtCV)

        private fun setColors(cardColor: Int, textColor: Int) {
            val cardColorContext = ContextCompat.getColor(itemView.context, cardColor)
            val textColorContext = ContextCompat.getColor(itemView.context, textColor)

            courtCardView.setCardBackgroundColor(cardColorContext)

            courtName.setTextColor(textColorContext)
            courtType.setTextColor(textColorContext)
            courtPrice.setTextColor(textColorContext)
            courtAvailability.setTextColor(textColorContext)
        }

//TODO MIGLIORALO
        fun bind(court: Court) {
            courtName.text = court.name
            courtType.text = "${court.idSport}"
            courtPrice.text = "hour charge: ${court.hourCharge}"
            courtAvailability.text = "Disponibile"
            val reservationStatus = isCourtReserved(court.id)
            when (reservationStatus) {
                0 -> { //court not reserved
                    setColors(R.color.green_500, R.color.white)
                }
                1 -> { //court reserved from others
                    setColors(R.color.grey, R.color.white)
                }
                else -> { //court reserved from you
                    setColors(R.color.white, R.color.green_500)
                }
            }
            val isReservedByYou = reservationStatus == 2
            if (reservationStatus!=0) {
                courtCardView.elevation = 0F
                courtCardView.isClickable = isReservedByYou
                courtAvailability.text = if (isReservedByYou) "riservato da te" else "riservato"
            }
        }


        fun unbind() {
            courtName.text = ""
            courtType.text = ""
            courtPrice.text = ""
            courtAvailability.text = ""
            setColors(R.color.white, R.color.white)
        }
    }

}