package it.polito.mad.g26.playingcourtreservation.repository.impl

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.g26.playingcourtreservation.model.Notification
import it.polito.mad.g26.playingcourtreservation.repository.NotificationRepository
import it.polito.mad.g26.playingcourtreservation.repository.UserRepository
import it.polito.mad.g26.playingcourtreservation.util.FirestoreCollections
import it.polito.mad.g26.playingcourtreservation.util.UiState
import it.polito.mad.g26.playingcourtreservation.util.await
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val userRepository: UserRepository
) : NotificationRepository {

    override suspend fun getCurrentUserNotification(): UiState<List<Notification>> {
        val currentUserId = userRepository.currentUser!!.uid
        return try {
            Log.d(TAG, "Performing getCurrentUserNotification for the user with id $currentUserId")
            val result = db.collection(FirestoreCollections.NOTIFICATIONS)
                .whereEqualTo("userId", currentUserId)
                .get().await()
            Log.d(
                TAG,
                "getCurrentUserNotification $currentUserId: ${result.documents.size} results"
            )
            val notifications = arrayListOf<Notification>()
            for (document in result) {
                val notification = document.toObject(Notification::class.java)
                notifications.add(notification)
            }
            UiState.Success(notifications)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error while performing getCurrentUserNotification $currentUserId: ${e.message}",
                e
            )
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun saveNotification(notification: Notification): UiState<Unit> {
        return try {
            Log.d(TAG, "Performing saveNotification of $notification")
            val documentRef = db.collection(FirestoreCollections.NOTIFICATIONS).document()
            notification.id = documentRef.id
            documentRef.set(notification).await()
            Log.d(
                TAG,
                "Notification document added to Firestore collection with ID ${notification.id}"
            )
            UiState.Success(Unit)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error while performing saveNotification of $notification: ${e.message}",
                e
            )
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun setNotificationAsRead(notificationId: String): UiState<Unit> {
        return try {
            Log.d(TAG, "Performing setNotificationAsRead of notification with id $notificationId")
            db.collection(FirestoreCollections.NOTIFICATIONS)
                .document(notificationId)
                .update("read", true).await()
            Log.d(TAG, "Notification $notificationId set as read")
            UiState.Success(Unit)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error while performing setNotificationAsRead of notification with id $notificationId: ${e.message}",
                e
            )
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun deleteNotificationById(notificationId: String): UiState<Unit> {
        return try {
            Log.d(TAG, "Performing deleteNotificationById of notification with id $notificationId")
            db.collection(FirestoreCollections.NOTIFICATIONS)
                .document(notificationId)
                .delete().await()
            Log.d(TAG, "Notification $notificationId deleted successfully")
            UiState.Success(Unit)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error while performing deleteNotificationById of notification with id $notificationId: ${e.message}",
                e
            )
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun deleteAllCurrentUserNotifications(): UiState<Unit> {
        val currentUserId = userRepository.currentUser!!.uid
        return try {
            Log.d(
                TAG,
                "Performing deleteAllCurrentUserNotifications for the user with id $currentUserId"
            )
            val query = db.collection(FirestoreCollections.NOTIFICATIONS)
                .whereEqualTo("userId", currentUserId)
            val batch = db.batch()
            query.get().await().forEach { document ->
                batch.delete(document.reference)
            }
            batch.commit().await()
            Log.d(TAG, "All notifications for user $currentUserId have been deleted")
            UiState.Success(Unit)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error while performing deleteNotificationById for the user with id $currentUserId: ${e.message}",
                e
            )
            UiState.Failure(e.localizedMessage)
        }
    }

    companion object {
        private const val TAG = "NotificationRepository"
    }
}
