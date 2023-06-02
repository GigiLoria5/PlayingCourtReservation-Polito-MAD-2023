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
import it.polito.mad.g26.playingcourtreservation.util.makeInvisible
import it.polito.mad.g26.playingcourtreservation.util.makeVisible

class UserSkillsAdapter(
    private var skills: List<Skill>,
    private val isEditable: Boolean = false
) :
    RecyclerView.Adapter<UserSkillsAdapter.UserSkillsViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserSkillsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.profile_skill, parent, false
        )
        return UserSkillsViewHolder(view)
    }

    fun getSkills(): List<Skill> {
        return skills.filter { it.rating != 0.0f }
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
        private val sportName = view.findViewById<TextView>(R.id.sport_name)
        private val rating = view.findViewById<RatingBar>(R.id.rating)
        private val buttonDelete = view.findViewById<MaterialButton>(R.id.delete_rating)

        fun bind(skill: Skill) {
            sportName.text = skill.sportName
            rating.rating = skill.rating
            if (!isEditable) {
                buttonDelete.makeInvisible()
                return
            }
            buttonDelete.makeVisible()
            rating.setOnRatingBarChangeListener { _, newRating, _ ->
                val updatedSkill = skill.copy(rating = newRating)
                updateSkillInList(skill, updatedSkill)
            }
            buttonDelete.setOnClickListener {
                val updatedSkill = skill.copy(rating = 0f)
                updateSkillInList(skill, updatedSkill)
                rating.rating = 0f
            }
        }
    }

}
