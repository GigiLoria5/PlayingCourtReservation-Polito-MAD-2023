package it.polito.mad.g26.playingcourtreservation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.model.Review

class ReviewsAdapter(
    private var reviews: List<Review>
) : RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewsViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.court_reviews_review_item, parent, false)
        return ReviewsViewHolder(v)
    }

    override fun onBindViewHolder(holder: ReviewsViewHolder, position: Int) {
        holder.bind(reviews[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateReviews(updatedCollection: List<Review>) {
        this.reviews = updatedCollection
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = reviews.size

    inner class ReviewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val date = view.findViewById<TextView>(R.id.reviewDateTV)
        private val rating = view.findViewById<RatingBar>(R.id.rating)
        private val text = view.findViewById<TextView>(R.id.reviewTextTV)

        fun bind(review: Review) {
            date.text = review.date
            rating.rating = review.rating
            text.text = review.text
        }
    }
}