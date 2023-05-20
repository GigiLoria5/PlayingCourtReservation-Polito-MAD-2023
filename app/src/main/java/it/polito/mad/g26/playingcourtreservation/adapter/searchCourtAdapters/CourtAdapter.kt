package it.polito.mad.g26.playingcourtreservation.adapter.searchCourtAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.model.CourtWithDetails
import it.polito.mad.g26.playingcourtreservation.model.custom.CourtReviewsSummary

class CourtAdapter(
    private val collection: List<CourtWithDetails>,
    private val reviews: List<CourtReviewsSummary>,
    private val isCourtAvailable: (Int) -> Boolean,
    private val navigateToReviews: (Int) -> Unit,
    private val navigateToChooseServices: (Int, String, Float, String) -> Unit
) :
    RecyclerView.Adapter<CourtAdapter.CourtViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourtViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.court_item, parent, false)
        return CourtViewHolder(view)
    }


    override fun onBindViewHolder(holder: CourtViewHolder, position: Int) {
        val court = collection[position]
        val courtReviews = reviews.find { it.courtId == court.court.id } ?: CourtReviewsSummary(
            court.court.id,
            0.0,
            0
        )
        holder.bind(collection[position], courtReviews)
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

        fun bind(courtWithDetails: CourtWithDetails, courtReviews: CourtReviewsSummary) {
            courtReviewsTV.text = itemView.context.getString(
                R.string.reviews_summary,
                String.format("%.2f", courtReviews.avg),
                courtReviews.count.toString()
            )
            courtName.text = courtWithDetails.court.name
            courtType.text = courtWithDetails.sport.name
            courtPrice.text = itemView.context.getString(
                R.string.hour_charge_court_short,
                String.format("%.2f", courtWithDetails.court.hourCharge)
            )

            if (courtReviews.count > 0) {
                courtReviewsMCV.setOnClickListener {
                    navigateToReviews(courtWithDetails.court.id)
                }
            } else {
                courtReviewsMCV.isClickable = false
                courtReviewsMCV.alpha = 0.7f
            }

            when (isCourtAvailable(courtWithDetails.court.id)) {
                true -> {
                    setColors(R.color.grey_light_2, R.color.custom_black, R.color.grey, 1f)
                    courtAvailability.text =
                        itemView.context.getString(R.string.court_available)
                    courtMCV.setOnClickListener {
                        val court = courtWithDetails.court
                        val sport = courtWithDetails.sport
                        navigateToChooseServices(
                            court.id, court.name, court.hourCharge, sport.name
                        )
                    }
                }

                false -> {
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