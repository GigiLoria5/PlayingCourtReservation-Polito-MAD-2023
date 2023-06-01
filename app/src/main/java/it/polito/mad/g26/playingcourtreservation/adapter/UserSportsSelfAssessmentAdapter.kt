package it.polito.mad.g26.playingcourtreservation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.model.Skill

class UserSportsSelfAssessmentAdapter(
    private val sportAssessments: List<Skill>
) :
    RecyclerView.Adapter<UserSportsSelfAssessmentAdapter.UserSportsSelfAssessmentViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserSportsSelfAssessmentViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.show_profile_sport_item, parent, false)
        return UserSportsSelfAssessmentViewHolder(v)
    }

    // Need to know the max value of position
    override fun getItemCount(): Int {
        return sportAssessments.size
    }

    // Called after viewHolder are created, to put data into them
    override fun onBindViewHolder(holder: UserSportsSelfAssessmentViewHolder, position: Int) {
        holder.bind(sportAssessments[position])
    }

    inner class UserSportsSelfAssessmentViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val sportName = v.findViewById<TextView>(R.id.sport_title_show_profile)
        private val sportRating = v.findViewById<RatingBar>(R.id.sport_rating_show_profile)

        fun bind(skill: Skill) {
            sportName.text = skill.sportName
            sportRating.rating = skill.rating
        }
    }

}
