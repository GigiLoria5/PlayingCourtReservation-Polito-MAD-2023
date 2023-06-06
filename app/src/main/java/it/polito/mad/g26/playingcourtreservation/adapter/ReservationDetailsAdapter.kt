package it.polito.mad.g26.playingcourtreservation.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.model.User
import it.polito.mad.g26.playingcourtreservation.util.setImageFromByteArray

class ReservationDetailsAdapter(
    private val l: List<User>,
    private val mode: Int,
    private val sport: String,
    private val navigateToShowProfileFragment: (String) -> Unit,
    private var userPicturesMap: HashMap<String, ByteArray?>,
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
        val user = l[position]
        val userPicture = userPicturesMap[user.id]
        when (holder) {
            is ReservationDetailsViewHolderParticipant -> holder.bind(
                l[position],
                sport,
                userPicture,
                position
            )

            is ReservationDetailsViewHolderRequester -> holder.bind(l[position], sport, userPicture)
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
        private val avatar = v.findViewById<ShapeableImageView>(R.id.avatar)

        fun bind(u: User, sport: String, userPicture: ByteArray?) {
            username.text = u.username
            roleAndAge.text = itemView.context.getString(
                R.string.role_and_age_concatenation,
                u.positionOrDefault(),
                u.ageOrDefault()
            )
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
            if (userPicture != null) {
                avatar.setImageFromByteArray(userPicture)
            } else {
                avatar.setImageDrawable(
                    AppCompatResources.getDrawable(
                        itemView.context,
                        R.drawable.profile_default
                    )
                )
            }
        }
    }

    inner class ReservationDetailsViewHolderParticipant(v: View) : RecyclerView.ViewHolder(v) {

        private val username = v.findViewById<TextView>(R.id.user_nameTV)
        private val roleAndAge = v.findViewById<TextView>(R.id.user_roleAndAgeTV)
        private val city = v.findViewById<TextView>(R.id.user_cityTV)
        private val rating = v.findViewById<RatingBar>(R.id.ratingRB)
        private val card = v.findViewById<MaterialCardView>(R.id.userMCV)
        private val avatar = v.findViewById<ShapeableImageView>(R.id.avatar)

        fun bind(u: User, sport: String, userPicture: ByteArray?, position: Int) {
            username.text = u.username
            roleAndAge.text = itemView.context.getString(
                R.string.role_and_age_concatenation,
                u.positionOrDefault(),
                u.ageOrDefault()
            )
            city.text = u.locationOrDefault()
            val rate = u.skills.find { it.sportName == sport }
            if (rate != null)
                rating.rating = rate.rating
            else rating.rating = 0f
            card.setOnClickListener {
                navigateToShowProfileFragment(u.id)
            }
            if (userPicture != null) {
                avatar.setImageFromByteArray(userPicture)
            } else {
                avatar.setImageDrawable(
                    AppCompatResources.getDrawable(
                        itemView.context,
                        R.drawable.profile_default
                    )
                )
            }
            if (position == 0)
                username.setTypeface(null, Typeface.BOLD)
        }
    }

}