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
import it.polito.mad.g26.playingcourtreservation.model.User
import it.polito.mad.g26.playingcourtreservation.model.timestampToDate

class ReviewsAdapter(
    private var reviews: List<Review>,
    private var userInformationMap: HashMap<String, User>
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
    fun updateReviews(updatedCollection: List<Review>, userInformationMap: HashMap<String, User>) {
        this.reviews = updatedCollection
        this.userInformationMap = userInformationMap
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = reviews.size

    inner class ReviewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val date = view.findViewById<TextView>(R.id.reviewDateTV)
        private val rating = view.findViewById<RatingBar>(R.id.rating)
        private val text = view.findViewById<TextView>(R.id.reviewTextTV)

        fun bind(review: Review) {
            date.text = timestampToDate(review.date)
            rating.rating = review.rating
            text.text = review.text
            // TODO: add user username
            val userReviewUsername = userInformationMap[review.userId]!!.username
            println(userReviewUsername)
        }
    }
}