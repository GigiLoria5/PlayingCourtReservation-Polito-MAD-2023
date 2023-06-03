package it.polito.mad.g26.playingcourtreservation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.model.User

class ReservationDetailsAdapter(
    private val l: List<User>,
    private val mode: Int,
    private val sport: String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //Inflater of the parent transform the xml of a row of the recyclerView into a view
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return if (mode == 1) {
            val v =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.reservation_details_profile, parent, false)
            ReservationDetailsViewHolderApplicant(v)
        } else {
            val v =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.reservation_details_applicant_profile, parent, false)
            ReservationDetailsViewHolderInvitees(v)
        }
    }

    //Need to know the max value of position
    override fun getItemCount(): Int {
        return l.size
    }

    //called after viewHolder are created, to put data into them
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ReservationDetailsViewHolderApplicant -> holder.bind(l[position], sport)
            is ReservationDetailsViewHolderInvitees -> holder.bind(l[position], sport)
        }
    }

    inner class ReservationDetailsViewHolderInvitees(v: View) : RecyclerView.ViewHolder(v) {

        private val username = v.findViewById<TextView>(R.id.username)
        private val role = v.findViewById<TextView>(R.id.user_roleTV)
        private val rating = v.findViewById<RatingBar>(R.id.ratingRB)
        private val acceptButton = v.findViewById<MaterialCardView>(R.id.userAddActionMCV)
        private val removeButton = v.findViewById<MaterialCardView>(R.id.userRemoveActionMCV)
        fun bind(u: User, sport: String) {
            username.text = u.username
            role.text = u.position
            val rate = u.skills.find { it.first == sport }
            if (rate != null)
                rating.rating = rate.second.toFloat()
            else rating.rating = 0f
            acceptButton.setOnClickListener {

            }
            removeButton.setOnClickListener {

            }
        }
    }

    inner class ReservationDetailsViewHolderApplicant(v: View) : RecyclerView.ViewHolder(v) {

        private val username = v.findViewById<TextView>(R.id.username)
        private val role = v.findViewById<TextView>(R.id.user_roleTV)
        private val rating = v.findViewById<RatingBar>(R.id.ratingRB)

        fun bind(u: User, sport: String) {
            username.text = u.username
            role.text = u.position
            val rate = u.skills.find { it.first == sport }
            if (rate != null)
                rating.rating = rate.second.toFloat()
            else rating.rating = 0f
        }
    }

}