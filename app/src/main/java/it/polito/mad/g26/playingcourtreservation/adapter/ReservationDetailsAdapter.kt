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
    private val sport: String,
    private val navigateToShowProfileFragment: (String) -> Unit,
    private val changeUser: (User) -> Unit,
    private val removeUser: (User) -> Unit,
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
                    .inflate(R.layout.reservation_details_profile_participants, parent, false)
            ReservationDetailsViewHolderParticipant(v)
        } else {
            val v =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.reservation_details_profile_requester, parent, false)
            ReservationDetailsViewHolderRequester(v)
        }
    }

    //Need to know the max value of position
    override fun getItemCount(): Int {
        return l.size
    }

    //called after viewHolder are created, to put data into them
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ReservationDetailsViewHolderParticipant -> holder.bind(l[position], sport)
            is ReservationDetailsViewHolderRequester -> holder.bind(l[position], sport)
        }
    }

    inner class ReservationDetailsViewHolderRequester(v: View) : RecyclerView.ViewHolder(v) {

        private val username = v.findViewById<TextView>(R.id.user_nameTV)
        private val roleAndAge = v.findViewById<TextView>(R.id.user_roleAndAgeTV)
        private val rating = v.findViewById<RatingBar>(R.id.ratingRB)
        private val city = v.findViewById<TextView>(R.id.user_cityTV)
        private val acceptButton = v.findViewById<MaterialCardView>(R.id.userAddActionMCV)
        private val removeButton = v.findViewById<MaterialCardView>(R.id.userRemoveActionMCV)
        private val card = v.findViewById<MaterialCardView>(R.id.userMCV)
        fun bind(u: User, sport: String) {
            username.text = u.username
            roleAndAge.text = u.position + ", " + u.ageOrDefault() + " y.o."
            city.text = u.location
            val rate = u.skills.find { it.sportName == sport }
            if (rate != null)
                rating.rating = rate.rating
            else rating.rating = 0f
            card.setOnClickListener {
                navigateToShowProfileFragment(u.id)
            }
            acceptButton.setOnClickListener {
                changeUser(u)
            }
            removeButton.setOnClickListener {
                removeUser(u)
            }
        }
    }

    inner class ReservationDetailsViewHolderParticipant(v: View) : RecyclerView.ViewHolder(v) {

        private val username = v.findViewById<TextView>(R.id.user_nameTV)
        private val roleAndAge = v.findViewById<TextView>(R.id.user_roleAndAgeTV)
        private val city = v.findViewById<TextView>(R.id.user_cityTV)
        private val rating = v.findViewById<RatingBar>(R.id.ratingRB)
        private val card = v.findViewById<MaterialCardView>(R.id.userMCV)
        fun bind(u: User, sport: String) {
            username.text = u.username
            roleAndAge.text = u.position + ", " + u.ageOrDefault() + " y.o."
            city.text = u.locationOrDefault()
            val rate = u.skills.find { it.sportName == sport }
            if (rate != null)
                rating.rating = rate.rating
            else rating.rating = 0f
            card.setOnClickListener {
                navigateToShowProfileFragment(u.id)
            }
        }
    }

}