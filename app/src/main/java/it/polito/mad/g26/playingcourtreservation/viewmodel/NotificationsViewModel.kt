package it.polito.mad.g26.playingcourtreservation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.g26.playingcourtreservation.model.Notification
import it.polito.mad.g26.playingcourtreservation.repository.NotificationRepository
import it.polito.mad.g26.playingcourtreservation.repository.ReservationRepository
import it.polito.mad.g26.playingcourtreservation.util.UiState
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val reservationRepository: ReservationRepository
) : ViewModel() {

    private val _loadingState = MutableLiveData<UiState<Unit>>()
    val loadingState: LiveData<UiState<Unit>>
        get() = _loadingState

    private var _notifications: List<Notification> = listOf()
    val notifications: List<Notification>
        get() = _notifications

    fun loadNotifications() = viewModelScope.launch {
        _loadingState.value = UiState.Loading
        // Get Sport Center information and Courts reviews in parallel
        val notificationsDeferred = async { notificationRepository.getCurrentUserNotification() }
        val notificationsState = notificationsDeferred.await()
        // Check sport center result
        if (notificationsState is UiState.Failure) {
            _loadingState.value = notificationsState
            return@launch
        }
        _notifications = (notificationsState as UiState.Success).result
        _loadingState.value = UiState.Success(Unit)
    }

    fun deleteNotification(notificationId: String) = viewModelScope.launch {
        _loadingState.value = UiState.Loading
        val state = notificationRepository.deleteNotificationById(notificationId)
        if (state is UiState.Failure) {
            _loadingState.value = state
            return@launch
        }
        loadNotifications()
    }

    fun addNotification(notification: Notification) = viewModelScope.launch {
        _loadingState.value = UiState.Loading
        val state = notificationRepository.saveNotification(notification)
        if (state is UiState.Failure) {
            _loadingState.value = state
            return@launch
        }
        loadNotifications() // to update the notifications
    }

    fun deleteAllCurrentUserNotifications() = viewModelScope.launch {
        _loadingState.value = UiState.Loading
        val state = notificationRepository.deleteAllCurrentUserNotifications()
        if (state is UiState.Failure) {
            _loadingState.value = state
            return@launch
        }
        loadNotifications() // to update the notifications
    }

    private val _loadingStateReservation = MutableLiveData<UiState<Unit>>()
    val loadingStateReservation: LiveData<UiState<Unit>>
        get() = _loadingStateReservation

    fun findReservation(reservationId: String) = viewModelScope.launch {
        _loadingStateReservation.value = UiState.Loading
        val reservationDeferred = async { reservationRepository.getReservationById(reservationId) }
        val reservationState = reservationDeferred.await()
        if (reservationState is UiState.Failure) {
            _loadingStateReservation.value = reservationState
            return@launch
        }
        _loadingStateReservation.value = UiState.Success(Unit)

    }

}
