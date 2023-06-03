package it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.g26.playingcourtreservation.model.Notification
import it.polito.mad.g26.playingcourtreservation.repository.NotificationRepository
import it.polito.mad.g26.playingcourtreservation.model.User
import it.polito.mad.g26.playingcourtreservation.repository.UserRepository
import it.polito.mad.g26.playingcourtreservation.util.UiState
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _loginState = MutableLiveData<UiState<FirebaseUser>>()
    val loginState: LiveData<UiState<FirebaseUser>>
        get() = _loginState

    val currentUser: FirebaseUser?
        get() = userRepository.currentUser

    private var _currentUserInformation = MutableLiveData<UiState<User>>()
    val currentUserInformation: LiveData<UiState<User>>
        get() = _currentUserInformation

    init {
        if (currentUser != null) {
            _loginState.value = UiState.Success(currentUser!!)
        }
    }

    fun login() = viewModelScope.launch {
        _loginState.value = UiState.Loading
        val loginResultState = userRepository.login()
        delay(500)
        _loginState.value = loginResultState
    }

    fun getCurrentUserInformation() = viewModelScope.launch {
        _currentUserInformation.value = UiState.Loading
        val userInformationState = userRepository.getCurrentUserInformation()
        delay(250)
        _currentUserInformation.value = userInformationState
    }

    private val _loadingState = MutableLiveData<UiState<Unit>>()
    val loadingState: LiveData<UiState<Unit>>
        get() = _loadingState

    private var _notifications: List<Notification> = listOf()
    val notifications: List<Notification>
        get() = _notifications

    fun loadNotifications() = viewModelScope.launch {
        _loadingState.value = UiState.Loading
        val notificationsDeferred = async { notificationRepository.getCurrentUserNotification() }
        val notificationsState = notificationsDeferred.await()
        // Check result
        if (notificationsState is UiState.Failure) {
            _loadingState.value = notificationsState
            return@launch
        }
        _notifications = (notificationsState as UiState.Success).result
        _loadingState.value = UiState.Success(Unit)
    }

}
