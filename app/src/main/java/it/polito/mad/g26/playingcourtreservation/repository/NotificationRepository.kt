package it.polito.mad.g26.playingcourtreservation.repository

import it.polito.mad.g26.playingcourtreservation.model.Notification
import it.polito.mad.g26.playingcourtreservation.util.UiState

interface NotificationRepository {
    suspend fun getCurrentUserNotification(): UiState<List<Notification>>

    suspend fun saveNotification(notification: Notification): UiState<Unit>

    suspend fun setNotificationAsRead(notificationId: String): UiState<Unit>

    suspend fun deleteNotificationById(notificationId: String): UiState<Unit>

    suspend fun deleteAllCurrentUserNotifications(): UiState<Unit>
}