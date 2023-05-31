package it.polito.mad.g26.playingcourtreservation.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Notification(
    val id: String = "",
    val userId: String = "",
    val reservationId: String = "",
    @ServerTimestamp
    val timestamp: Timestamp = Timestamp.now(),
    val message: String = "",
    var isRead: Boolean = false
) {
    companion object {
        fun participationRequest(userId: String, reservationId: String): Notification {
            val message = "New request to join your match!"
            return Notification(userId = userId, reservationId = reservationId, message = message)
        }

        fun participantAbandoned(userId: String, reservationId: String): Notification {
            val message = "A participant has abandoned your match."
            return Notification(userId = userId, reservationId = reservationId, message = message)
        }

        fun invitationResponse(
            userId: String,
            reservationId: String,
            accepted: Boolean
        ): Notification {
            val message = if (accepted) {
                "Your invitation was accepted by the recipient."
            } else {
                "Your invitation was rejected by the recipient."
            }
            return Notification(userId = userId, reservationId = reservationId, message = message)
        }

        fun matchUpdated(userId: String, reservationId: String): Notification {
            val message = "Match details have been updated."
            return Notification(userId = userId, reservationId = reservationId, message = message)
        }

        fun matchCancelled(userId: String, reservationId: String): Notification {
            val message = "The match you registered for has been cancelled."
            return Notification(userId = userId, reservationId = reservationId, message = message)
        }

        fun matchInvitation(userId: String, reservationId: String): Notification {
            val message = "You have been invited to join a match!"
            return Notification(userId = userId, reservationId = reservationId, message = message)
        }

        fun requestResponse(
            userId: String,
            reservationId: String,
            accepted: Boolean
        ): Notification {
            val message = if (accepted) {
                "Your request to join a match has been accepted."
            } else {
                "Your request to join a match has been rejected."
            }
            return Notification(userId = userId, reservationId = reservationId, message = message)
        }
    }
}
