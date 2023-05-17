package it.polito.mad.g26.playingcourtreservation.adapter.searchCourtAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.model.CourtWithDetails //TODO ORA STAI UTILIZZANDO QUELLO CHE HA CREATO LUIGI, ATTENTO CASOMAI CI SONO DIFFERENZE
import it.polito.mad.g26.playingcourtreservation.enums.CourtStatus

class CourtAdapter(
    private val collection: List<CourtWithDetails>,
    private val courtReservationState: (Int) -> CourtStatus,
    private val confirmReservation: (CourtWithDetails)->Unit

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

        fun bind(courtWithDetails: CourtWithDetails) {
            courtName.text = courtWithDetails.court.name
            courtType.text = courtWithDetails.sport.name
            courtPrice.text = itemView.context.getString(
                R.string.hour_charge_court,
                String.format("%.2f", courtWithDetails.court.hourCharge)
            )

            when (courtReservationState(courtWithDetails.court.id)) {
                CourtStatus.AVAILABLE -> {
                    setColors(R.color.green_500, R.color.white)
                    courtAvailability.text =
                        itemView.context.getString(R.string.court_available)
                    courtCardView.setOnClickListener {
                        confirmReservation(courtWithDetails)
                    }
                }
                CourtStatus.NOT_AVAILABLE -> {
                    setColors(R.color.grey, R.color.white)
                    courtAvailability.text =
                        itemView.context.getString(R.string.court_not_available)
                    courtCardView.elevation = 0F
                    courtCardView.isClickable = false
                }
            }
        }

        fun unbind() {
            courtName.text = ""
            courtType.text = ""
            courtPrice.text = ""
            courtAvailability.text = ""
            courtCardView.setOnClickListener(null)
            setColors(R.color.white, R.color.white)
        }
    }

}