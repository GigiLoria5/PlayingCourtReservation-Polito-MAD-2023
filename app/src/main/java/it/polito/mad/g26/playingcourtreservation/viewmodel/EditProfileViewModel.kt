package it.polito.mad.g26.playingcourtreservation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.g26.playingcourtreservation.model.Skill
import it.polito.mad.g26.playingcourtreservation.model.User
import it.polito.mad.g26.playingcourtreservation.repository.UserRepository
import it.polito.mad.g26.playingcourtreservation.util.SportNames
import it.polito.mad.g26.playingcourtreservation.util.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

    // Update User Information
    private val _updateState = MutableLiveData<UiState<Unit>>()
    val updateState: LiveData<UiState<Unit>>
        get() = _updateState

    fun updateCurrentUserInformation(updatedUserInformation: User) = viewModelScope.launch {
        _updateState.value = UiState.Loading
        val resultState = userRepository.updateCurrentUserInformation(updatedUserInformation)
        delay(500)
        _updateState.value = resultState
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
