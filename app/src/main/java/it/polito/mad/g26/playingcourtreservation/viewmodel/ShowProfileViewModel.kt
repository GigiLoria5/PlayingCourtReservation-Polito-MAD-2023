package it.polito.mad.g26.playingcourtreservation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.g26.playingcourtreservation.model.User
import it.polito.mad.g26.playingcourtreservation.repository.UserRepository
import it.polito.mad.g26.playingcourtreservation.util.UiState
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShowProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _loadingState = MutableLiveData<UiState<Unit>>()
    val loadingState: LiveData<UiState<Unit>>
        get() = _loadingState

    private var _userInformation = User()
    val userInformation: User
        get() = _userInformation

    private var _userImageData: ByteArray? = null
    val userImageData: ByteArray?
        get() = _userImageData

    fun loadCurrentUserInformation(userId: String?) = viewModelScope.launch {
        _loadingState.value = UiState.Loading
        val deferredUserInfoState =
            if (userId == null)
                async { userRepository.getCurrentUserInformation() }
            else
                async { userRepository.getUserInformationById(userId) }
        val deferredUserImageState = async {
            userRepository.downloadUserImage(userId ?: userRepository.currentUser!!.uid)
        }
        val userInfoState = deferredUserInfoState.await()
        val userImgUpdateState = deferredUserImageState.await()
        if (userInfoState is UiState.Failure) {
            _loadingState.value = userInfoState
            return@launch
        }
        if (userImgUpdateState is UiState.Failure) {
            _loadingState.value = userImgUpdateState
            return@launch
        }
        _userInformation = (userInfoState as UiState.Success).result
        _userImageData = (userImgUpdateState as UiState.Success).result
        _loadingState.value = UiState.Success(Unit)
    }
}