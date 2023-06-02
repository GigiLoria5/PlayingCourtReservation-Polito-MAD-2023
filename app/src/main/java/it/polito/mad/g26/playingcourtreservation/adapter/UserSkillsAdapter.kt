package it.polito.mad.g26.playingcourtreservation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.model.Skill

class UserSkillsAdapter(
    private var skills: List<Skill>,
    private val isEditable: Boolean = false
) :
    RecyclerView.Adapter<UserSkillsAdapter.UserSkillsViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserSkillsViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.show_profile_sport_item, parent, false)
        return UserSkillsViewHolder(v)
    }

    // Need to know the max value of position
    override fun getItemCount(): Int {
        return skills.size
    }

    // Called after viewHolder are created, to put data into them
    override fun onBindViewHolder(holder: UserSkillsViewHolder, position: Int) {
        holder.bind(skills[position])
    }

    private fun updateSkillInList(oldSkill: Skill, newSkill: Skill) {
        val index = skills.indexOfFirst { it.sportName == oldSkill.sportName }
        if (index >= 0) {
            val updatedList = skills.toMutableList()
            updatedList[index] = newSkill
            skills = updatedList.toList()
        }
    }

    inner class UserSkillsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val sportName = view.findViewById<TextView>(R.id.sport_title_show_profile)
        private val rating = view.findViewById<RatingBar>(R.id.sport_rating_show_profile)
        private val buttonDelete =
            if (isEditable) view.findViewById<MaterialButton>(R.id.sport_delete_rating)
            else null

        fun bind(skill: Skill) {
            sportName.text = skill.sportName
            rating.rating = skill.rating
            if (isEditable) {
                rating.setOnRatingBarChangeListener { _, newRating, _ ->
                    val updatedSkill = skill.copy(rating = newRating)
                    updateSkillInList(skill, updatedSkill)
                }
                buttonDelete!!.setOnClickListener {
                    val updatedSkill = skill.copy(rating = 0f)
                    updateSkillInList(skill, updatedSkill)
                    rating.rating = 0f
                }
            }
        }
    }

}
