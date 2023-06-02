package it.polito.mad.g26.playingcourtreservation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.model.User

class InviteUserAdapter(
    private var collection: List<User>,
    private val isUserIdInvited: (String) -> Boolean,
    private val sport: String
) :
    RecyclerView.Adapter<InviteUserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.invite_users_user_item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = collection[position]

        holder.bind(collection[position], isUserIdInvited(user.id))
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCollection(
        updatedCollection: List<User>
    ) {
        this.collection = updatedCollection
        notifyDataSetChanged()
    }

    override fun onViewRecycled(holder: UserViewHolder) {
        holder.unbind()
    }

    override fun getItemCount() = collection.size

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userNameTV = itemView.findViewById<TextView>(R.id.userNameTV)
        private val userPositionAgeTV = itemView.findViewById<TextView>(R.id.userPositionAgeTV)
        private val userCityTV = itemView.findViewById<TextView>(R.id.userCityTV)
        private val userAvailabilityTV = itemView.findViewById<TextView>(R.id.userAvailabilityTV)
        private val ratingRB = itemView.findViewById<RatingBar>(R.id.ratingRB)
        private val userActionMCV = itemView.findViewById<MaterialCardView>(R.id.userActionMCV)
        private val customSearchIconIV = itemView.findViewById<ImageView>(R.id.customSearchIconIV)

        fun bind(user: User, isUserIdInvited: Boolean) {

            userNameTV.text = user.username
            if (user.position == null) {
                //if one info is null, all infos are null
                userPositionAgeTV.text = itemView.context.getString(R.string.not_specified_info)
                userCityTV.text = ""
            } else {
                val age = user.getAgeOrDefault()
                userPositionAgeTV.text =
                    itemView.context.getString(R.string.user_position_age, user.position, age)
                userCityTV.text = user.location
            }
            ratingRB.rating = user.skills.find { it.sportName == sport }?.rating ?: 0f

            when (isUserIdInvited) {
                false -> {
                    userAvailabilityTV.text = itemView.context.getString(R.string.user_available)
                    customSearchIconIV.setImageDrawable(AppCompatResources.getDrawable(itemView.context,R.drawable.baseline_add_24_green))
                    userActionMCV.setCardBackgroundColor(itemView.context.getColor(R.color.grey_light_2))
                }

                true -> {
                    userAvailabilityTV.text = itemView.context.getString(R.string.user_invited)
                    customSearchIconIV.setImageDrawable(AppCompatResources.getDrawable(itemView.context,R.drawable.baseline_check_24))
                    userActionMCV.setCardBackgroundColor(itemView.context.getColor(R.color.green_500))
                }
            }
        }

        fun unbind() {
            userNameTV.text = ""
            userPositionAgeTV.text = ""
            userAvailabilityTV.text = ""
            ratingRB.rating = 0f
            userActionMCV.setOnClickListener(null)
        }
    }

}
