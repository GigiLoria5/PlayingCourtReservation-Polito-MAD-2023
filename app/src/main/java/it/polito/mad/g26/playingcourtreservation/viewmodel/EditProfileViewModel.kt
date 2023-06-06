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
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
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

    fun updateCurrentUserInformation(updatedUserInformation: User, newUserAvatar: ByteArray?) =
        viewModelScope.launch {
            _updateState.value = UiState.Loading
            // Check if there are any changes
            userInformation.skills = userInformation.skills.sortedBy { it.rating }
            updatedUserInformation.skills = updatedUserInformation.skills.sortedBy { it.rating }
            if (newUserAvatar == null && userInformation == updatedUserInformation) {
                _updateState.value = UiState.Failure("Please make changes before saving")
                return@launch
            }
            // Check if username is unique
            val userWithUsernameState = userRepository
                .getUserInformationByUsername(updatedUserInformation.username)
            if (userWithUsernameState is UiState.Failure) {
                _updateState.value = userWithUsernameState
                return@launch
            }
            val userWithUsername = (userWithUsernameState as UiState.Success).result
            if (userWithUsername != null && userWithUsername.id != updatedUserInformation.id) {
                _updateState.value =
                    UiState.Failure("A user already exists with this username, choose another one")
                return@launch
            }
            // Update information and eventually avatar
            val deferredUserInfoUpdateState =
                async { userRepository.updateCurrentUserInformation(updatedUserInformation) }
            var deferredUserImgUpdateState: Deferred<UiState<Unit>>? = null
            if (newUserAvatar != null)
                deferredUserImgUpdateState = async { userRepository.updateUserImage(newUserAvatar) }
            val userInfoUpdateState = deferredUserInfoUpdateState.await()
            val userImgUpdateState = deferredUserImgUpdateState?.await() ?: UiState.Success(Unit)
            if (userInfoUpdateState is UiState.Failure) {
                _updateState.value = userInfoUpdateState
                return@launch
            }
            if (userImgUpdateState is UiState.Failure) {
                _updateState.value = userImgUpdateState
                return@launch
            }
            _updateState.value = UiState.Success(Unit)
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
