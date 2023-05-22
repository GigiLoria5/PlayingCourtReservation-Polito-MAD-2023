package it.polito.mad.g26.playingcourtreservation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.g26.playingcourtreservation.R

class ShowProfileAdapter (private val list: List<String>, private var rating : List<Float>) :
    RecyclerView.Adapter<ShowProfileAdapter.ShowProfileViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowProfileViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.show_profile_sport_recycler_view, parent, false)
        return ShowProfileViewHolder(v)
    }

    //Need to know the max value of position
    override fun getItemCount(): Int {
        return rating.size
    }

    //called after viewHolder are created, to put data into them
    override fun onBindViewHolder(holder: ShowProfileViewHolder, position: Int) {
            holder.bind(list[position],rating[position])
    }

    inner class ShowProfileViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        private val text = v.findViewById<TextView>(R.id.sport_title_show_profile)
        private val starRating= v.findViewById<RatingBar>(R.id.sport_rating_show_profile)

        fun bind(s: String, r:Float) {
            text.text=s
            starRating.rating=r
        }
    }
}