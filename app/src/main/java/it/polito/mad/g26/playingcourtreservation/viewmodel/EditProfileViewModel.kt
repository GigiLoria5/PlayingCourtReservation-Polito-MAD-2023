package it.polito.mad.g26.playingcourtreservation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.g26.playingcourtreservation.model.Skill
import it.polito.mad.g26.playingcourtreservation.model.User
import it.polito.mad.g26.playingcourtreservation.repository.UserRepository
import it.polito.mad.g26.playingcourtreservation.util.SportNames
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private var _userInformation = User()
    val userInformation: User
        get() = _userInformation

    fun initialize(user: User) {
        _userInformation = user
    }

    // Manage skill list
    private val allSkillsDefault = listOf(
        Skill(sportName = SportNames.FIVE_A_SIDE_FOOTBALL),
        Skill(sportName = SportNames.EIGHT_A_SIDE_FOOTBALL),
        Skill(sportName = SportNames.ELEVEN_A_SIDE_FOOTBALL),
        Skill(sportName = SportNames.BEACH_SOCCER),
        Skill(sportName = SportNames.FUTSAL)
    )

    fun getAllSkills(): List<Skill> {
        val userSkills = userInformation.skills
        val allSkills = mutableListOf<Skill>()
        allSkillsDefault.forEach { defaultSkill ->
            val userSkill = userSkills.firstOrNull { it.sportName == defaultSkill.sportName }
            allSkills.add(userSkill ?: defaultSkill)
        }
        return allSkills.sortedBy { -it.rating }
    }

}
