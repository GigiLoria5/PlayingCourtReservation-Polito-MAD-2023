package it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.g26.playingcourtreservation.model.Notification
import it.polito.mad.g26.playingcourtreservation.repository.NotificationRepository
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
