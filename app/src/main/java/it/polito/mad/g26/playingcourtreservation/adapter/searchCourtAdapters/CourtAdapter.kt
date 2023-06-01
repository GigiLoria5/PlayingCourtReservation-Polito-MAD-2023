package it.polito.mad.g26.playingcourtreservation.adapter.searchCourtAdapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.enums.CourtStatus
import it.polito.mad.g26.playingcourtreservation.model.Court
import it.polito.mad.g26.playingcourtreservation.model.Reservation
import it.polito.mad.g26.playingcourtreservation.model.Review
import it.polito.mad.g26.playingcourtreservation.model.avg

class CourtAdapter(
    private var collection: List<Court>,
    private val sportCenterId: String,
    private var reviews: HashMap<String, List<Review>>,
    private var reservations: HashMap<String, Reservation?>,
    private val navigateToReviews: (String, String) -> Unit,
    private val navigateToReservationDetails: (String) -> Unit,
    private val navigateToCompleteReservation: (String, String, Float, String) -> Unit
) :
    RecyclerView.Adapter<CourtAdapter.CourtViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourtViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_courts_court_item, parent, false)
        return CourtViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourtViewHolder, position: Int) {
        val court = collection[position]
        val courtReviews = reviews[court.id]!! // Each court has been added to the map
        val courtReservation = reservations[court.id]
        holder.bind(collection[position], courtReviews, courtReservation)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCollection(
        updatedCollection: List<Court>,
        updatedReviews: HashMap<String, List<Review>>,
        updatedReservations: HashMap<String, Reservation?>
    ) {
        this.collection = updatedCollection
        this.reviews = updatedReviews
        this.reservations = updatedReservations
        notifyDataSetChanged()
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
        private val courtReviewsMCV = itemView.findViewById<MaterialCardView>(R.id.courtReviewsMCV)
        private val courtReviewsTV = itemView.findViewById<TextView>(R.id.courtReviewsTV)
        private val courtMCV = itemView.findViewById<CardView>(R.id.courtMCV)

        private fun setColors(
            cardColor: Int,
            primaryTextColor: Int,
            secondaryTextColor: Int,
            alpha: Float
        ) {
            val cardColorContext = ContextCompat.getColor(itemView.context, cardColor)
            val primaryTextColorContext = ContextCompat.getColor(itemView.context, primaryTextColor)
            val secondaryTextColorContext =
                ContextCompat.getColor(itemView.context, secondaryTextColor)

            courtMCV.apply {
                this.alpha = alpha
                this.setCardBackgroundColor(cardColorContext)
            }
            courtName.apply {
                this.alpha = alpha
                this.setTextColor(primaryTextColorContext)
            }
            courtType.apply {
                this.alpha = alpha
                this.setTextColor(secondaryTextColorContext)
            }
            courtAvailability.apply {
                this.alpha = alpha
                this.setTextColor(secondaryTextColorContext)
            }
            courtPrice.apply {
                this.alpha = alpha
                this.setTextColor(primaryTextColorContext)
            }
        }

        fun bind(court: Court, courtReviews: List<Review>, courtReservation: Reservation?) {
            courtReviewsTV.text = itemView.context.getString(
                R.string.reviews_summary,
                String.format("%.2f", courtReviews.avg()),
                courtReviews.size.toString()
            )
            courtName.text = court.name
            courtType.text = court.sport

            val numberOfRequiredParticipants = Court.getSportTotParticipants(court.sport)
            val numberOfCurrentParticipants =
                (courtReservation?.participants?.size ?: 0) + 1 //1 is the organizer
            val numberOfMissingParticipants =
                numberOfRequiredParticipants - numberOfCurrentParticipants

            val courtStatus = when {
                courtReservation == null -> CourtStatus.AVAILABLE
                numberOfMissingParticipants > 0 -> CourtStatus.JOINABLE
                else -> CourtStatus.UNAVAILABLE
            }

            if (courtReviews.isNotEmpty()) {
                courtReviewsMCV.setOnClickListener {
                    navigateToReviews(court.id, sportCenterId)
                }
            } else {
                courtReviewsMCV.isClickable = false
                courtReviewsMCV.alpha = 0.7f
            }

            when (courtStatus) {
                CourtStatus.AVAILABLE -> {
                    setColors(R.color.grey_light_2, R.color.custom_black, R.color.grey, 1f)
                    courtPrice.text = itemView.context.getString(
                        R.string.hour_charge_court_short,
                        String.format("%.2f", court.hourCharge)
                    )
                    courtAvailability.text =
                        itemView.context.getString(R.string.court_available_to_reserve)

                    courtMCV.setOnClickListener {
                        val sport = court.sport
                        navigateToCompleteReservation(
                            court.id, court.name, court.hourCharge, sport
                        )
                    }
                }

                CourtStatus.JOINABLE -> {
                    setColors(R.color.grey_light_2, R.color.custom_black, R.color.grey, 1f)
                    courtPrice.text = itemView.context.getString(
                        R.string.hour_charge_court_short,
                        String.format("%.2f", courtReservation!!.amount)
                    )
                    courtAvailability.text =
                        itemView.context.getString(
                            R.string.court_available_to_join,
                            numberOfMissingParticipants.toString(),
                            if (numberOfMissingParticipants != 1) "s" else ""
                        )
                    courtMCV.setOnClickListener {
                        navigateToReservationDetails(courtReservation!!.id)
                    }
                }

                CourtStatus.UNAVAILABLE -> {
                    courtPrice.text = itemView.context.getString(
                        R.string.hour_charge_court_short,
                        String.format("%.2f", court.hourCharge)
                    )
                    setColors(R.color.grey_light_2, R.color.custom_black, R.color.grey, 0.7f)
                    courtAvailability.text =
                        itemView.context.getString(R.string.court_not_available)
                    courtMCV.elevation = 0F
                    courtMCV.isClickable = false
                }
            }
        }

        fun unbind() {
            courtName.text = ""
            courtType.text = ""
            courtPrice.text = ""
            courtAvailability.text = ""
            courtMCV.setOnClickListener(null)
            courtReviewsMCV.setOnClickListener(null)
            setColors(R.color.white, R.color.white, R.color.white, 0f)
        }
    }

}
