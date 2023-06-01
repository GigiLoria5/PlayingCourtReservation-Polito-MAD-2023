package it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.g26.playingcourtreservation.repository.UserRepository
import it.polito.mad.g26.playingcourtreservation.util.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _loginState = MutableLiveData<UiState<FirebaseUser>?>()
    val loginState: LiveData<UiState<FirebaseUser>?>
        get() = _loginState

    val currentUser: FirebaseUser?
        get() = userRepository.currentUser

    init {
        if (currentUser != null) {
            _loginState.value = UiState.Success(currentUser!!)
        }
    }

    fun login() = viewModelScope.launch {
        _loginState.value = UiState.Loading
        val result = userRepository.login()
        delay(1000)
        _loginState.value = result
    }

}
