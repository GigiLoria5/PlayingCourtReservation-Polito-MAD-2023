package it.polito.mad.g26.playingcourtreservation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.g26.playingcourtreservation.R

class ReservationDetailsAdapter(private val l: List<String>, private val mode: Int) :
    RecyclerView.Adapter<ReservationDetailsAdapter.ReservationDetailsViewHolder>() {

    //Inflater of the parent transform the xml of a row of the recyclerView into a view
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReservationDetailsViewHolder {
        return if (mode == 1) {
            val v =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.reservation_details_profile, parent, false)
            ReservationDetailsViewHolder(v)
        } else {
            val v =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.reservation_details_applicant_profile2, parent, false)
            ReservationDetailsViewHolder(v)
        }
    }

    //Need to know the max value of position
    override fun getItemCount(): Int {
        return l.size
    }

    //called after viewHolder are created, to put data into them
    override fun onBindViewHolder(holder: ReservationDetailsViewHolder, position: Int) {
        holder.bind(l[position])
    }

    inner class ReservationDetailsViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        private val text = v.findViewById<TextView>(R.id.username)

        fun bind(s: String) {
            text.text = s
        }
    }
}