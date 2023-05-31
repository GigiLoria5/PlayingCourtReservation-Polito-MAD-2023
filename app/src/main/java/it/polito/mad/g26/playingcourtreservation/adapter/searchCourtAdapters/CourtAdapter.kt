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
import it.polito.mad.g26.playingcourtreservation.model.Court
import it.polito.mad.g26.playingcourtreservation.model.Review
import it.polito.mad.g26.playingcourtreservation.model.avg

class CourtAdapter(
    private var collection: List<Court>,
    private val sportCenterId: String,
    private var reviews: HashMap<String, List<Review>>,
    private val isCourtAvailable: (String) -> Boolean,
    private val navigateToReviews: (String, String) -> Unit,
    private val navigateToChooseServices: (String, String, Float, String) -> Unit
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
        holder.bind(collection[position], courtReviews)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCollection(
        updatedCollection: List<Court>,
        updatedReviews: HashMap<String, List<Review>>
    ) {
        this.collection = updatedCollection
        this.reviews = updatedReviews
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

        fun bind(court: Court, courtReviews: List<Review>) {
            courtReviewsTV.text = itemView.context.getString(
                R.string.reviews_summary,
                String.format("%.2f", courtReviews.avg()),
                courtReviews.size.toString()
            )
            courtName.text = court.name
            courtType.text = court.sport
            courtPrice.text = itemView.context.getString(
                R.string.hour_charge_court_short,
                String.format("%.2f", court.hourCharge)
            )

            if (courtReviews.isNotEmpty()) {
                courtReviewsMCV.setOnClickListener {
                    navigateToReviews(court.id, sportCenterId)
                }
            } else {
                courtReviewsMCV.isClickable = false
                courtReviewsMCV.alpha = 0.7f
            }

            when (isCourtAvailable(court.id)) {
                true -> {
                    setColors(R.color.grey_light_2, R.color.custom_black, R.color.grey, 1f)
                    courtAvailability.text =
                        itemView.context.getString(R.string.court_available)
                    courtMCV.setOnClickListener {
                        val sport = court.sport
                        navigateToChooseServices(
                            court.id, court.name, court.hourCharge, sport
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
