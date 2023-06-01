package it.polito.mad.g26.playingcourtreservation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.g26.playingcourtreservation.model.User
import it.polito.mad.g26.playingcourtreservation.repository.UserRepository
import it.polito.mad.g26.playingcourtreservation.util.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShowProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userInformationState = MutableLiveData<UiState<User>>()
    val userInformationState: LiveData<UiState<User>>
        get() = _userInformationState

    fun loadCurrentUserInformation() = viewModelScope.launch {
        _userInformationState.value = UiState.Loading
        val resultState = userRepository.getCurrentUserInformation()
        delay(500)
        _userInformationState.value = resultState
    }
}