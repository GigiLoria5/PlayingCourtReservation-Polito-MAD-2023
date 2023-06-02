package it.polito.mad.g26.playingcourtreservation.repository.impl

import android.util.Log
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.g26.playingcourtreservation.model.Reservation
import it.polito.mad.g26.playingcourtreservation.model.Review
import it.polito.mad.g26.playingcourtreservation.model.SportCenter
import it.polito.mad.g26.playingcourtreservation.repository.ReservationRepository
import it.polito.mad.g26.playingcourtreservation.util.FirestoreCollections
import it.polito.mad.g26.playingcourtreservation.util.UiState
import it.polito.mad.g26.playingcourtreservation.util.await
import javax.inject.Inject

class ReservationRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
) : ReservationRepository {

    private val noReservationFound = "No existing reservation"

    override suspend fun getAllSportCenterReviews(sportCenter: SportCenter): UiState<List<Review>> {
        return try {
            val courtsIds = sportCenter.courts.map { it.id }
            Log.d(
                TAG,
                "Performing getAllSportCenterReviews for the sportCenter ${sportCenter.id} with the following courts: ${sportCenter.courts}"
            )
            val result = db.collection(FirestoreCollections.RESERVATIONS)
                .whereIn("courtId", courtsIds)
                .get().await()
            Log.d(
                TAG,
                "getAllSportCenterReviews ${sportCenter.id}: ${result.documents.size} results"
            )
            val reviews = arrayListOf<Review>()
            for (document in result) {
                val reservation = document.toObject(Reservation::class.java)
                reservation.id = document.id
                reservation.reviews.forEach { reviews.add(it) }
            }
            UiState.Success(reviews)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error while performing getAllSportCenterReviews ${sportCenter.id}: ${e.message}",
                e
            )
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun getCourtReviews(courtId: String): UiState<List<Review>> {
        return try {
            Log.d(TAG, "Performing getCourtReviews for the court with id: $courtId")
            val result = db.collection(FirestoreCollections.RESERVATIONS)
                .whereEqualTo("courtId", courtId)
                .get().await()
            Log.d(TAG, "getCourtReviews $courtId: ${result.documents.size} results")
            val reviews = arrayListOf<Review>()
            for (document in result) {
                val reservation = document.toObject(Reservation::class.java)
                reservation.id = document.id
                reservation.reviews.forEach { reviews.add(it) }
            }
            UiState.Success(reviews)
        } catch (e: Exception) {
            Log.e(TAG, "Error while performing getCourtReviews $courtId: ${e.message}", e)
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun getReservationReviewByUserId(
        reservationId: String,
        userId: String
    ): UiState<Review?> {
        return try {
            Log.d(
                TAG,
                "Performing getReservationReviewByUserId with reservationId: $reservationId and userId: $userId"
            )
            val result = db.collection(FirestoreCollections.RESERVATIONS)
                .document(reservationId)
                .get().await()
            Log.d(
                TAG,
                "getReservationReviewByUserId with reservationId: $reservationId and userId: $userId: reservation found=${result.exists()}"
            )
            val review = result.toObject(Reservation::class.java)!!
                .reviews.firstOrNull { it.userId == userId }
            UiState.Success(review)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error while performing getReservationReviewByUserId with reservationId: $reservationId and userId: $userId: ${e.message}",
                e
            )
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun saveReview(reservationId: String, review: Review): UiState<Unit> {
        return try {
            Log.d(TAG, "saveReview for reservation: $reservationId and review: $review")
            // Get the reservation
            val result = db.collection(FirestoreCollections.RESERVATIONS)
                .document(reservationId)
                .get().await()
            Log.d(
                TAG,
                "saveReview reservation: $reservationId found=${result.exists()}"
            )
            if (!result.exists()) // Reservation Id must be unique and existing
                UiState.Failure(noReservationFound)
            val reservation = result.toObject(Reservation::class.java)!!
            // Add the review
            reservation.reviews = reservation.reviews.plus(review)
            db.collection(FirestoreCollections.RESERVATIONS)
                .document(reservationId)
                .set(reservation).await()
            Log.d(TAG, "saveReview the review for the reservation: $reservationId has been added}")
            UiState.Success(Unit)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error while performing saveReview for reservation: $reservationId and review: $review: ${e.message}",
                e
            )
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun updateReview(reservationId: String, review: Review): UiState<Unit> {
        return try {
            Log.d(TAG, "updateReview for reservation: $reservationId and review: $review")
            // Get the reservation
            val result = db.collection(FirestoreCollections.RESERVATIONS)
                .document(reservationId)
                .get().await()
            Log.d(
                TAG,
                "updateReview reservation: $reservationId found=${result.exists()}"
            )
            if (!result.exists()) // Reservation Id must be unique and existing
                UiState.Failure(noReservationFound)
            val reservation = result.toObject(Reservation::class.java)!!
            // Update the review
            reservation.reviews = reservation.reviews.map {
                if (it.userId == review.userId) review
                else it
            }
            db.collection(FirestoreCollections.RESERVATIONS)
                .document(reservationId)
                .set(reservation).await()
            Log.d(
                TAG,
                "updateReview the review for the reservation: $reservationId has been added}"
            )
            UiState.Success(Unit)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error while performing updateReview for reservation: $reservationId and review: $review: ${e.message}",
                e
            )
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun deleteUserReview(reservationId: String, userId: String): UiState<Unit> {
        return try {
            Log.d(TAG, "deleteUserReview with reservation id: $reservationId and userId: $userId")
            // Get the reservation
            val result = db.collection(FirestoreCollections.RESERVATIONS)
                .document(reservationId)
                .get().await()
            Log.d(
                TAG,
                "deleteUserReview get reservation id: $reservationId: found=${result.exists()}"
            )
            if (!result.exists()) // Reservation Id must be unique and existing
                UiState.Failure(noReservationFound)
            // Remove the user review
            val reservation = result.toObject(Reservation::class.java)!!
            reservation.reviews = reservation.reviews.filter { it.userId != userId }
            // Save changes
            db.collection(FirestoreCollections.RESERVATIONS)
                .document(reservationId)
                .set(reservation).await()
            Log.d(TAG, "deleteUserReview: review deleted")
            UiState.Success(Unit)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error while performing deleteUserReview with reservation id: $reservationId and userId: $userId: ${e.message}",
                e
            )
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun getUserReservations(userId: String): UiState<List<Reservation>> {
        return try {
            Log.d(TAG, "getUserReservation for user with id: $userId")
            val result = db.collection(FirestoreCollections.RESERVATIONS)
                .whereEqualTo("userId", userId)
                .orderBy("date")
                .orderBy("time")
                .get().await()
            Log.d(TAG, "getUserReservation $userId: ${result.documents.size} result")
            val reservations = arrayListOf<Reservation>()
            for (document in result) {
                val reservation = document.toObject(Reservation::class.java)
                reservations.add(reservation)
            }
            UiState.Success(reservations)
        } catch (e: Exception) {
            Log.e(TAG, "Error while performing getUserReservation $userId: ${e.message}", e)
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun getReservationById(reservationId: String): UiState<Reservation> {
        return try {
            Log.d(TAG, "getReservationById with id: $reservationId")
            val result = db.collection(FirestoreCollections.RESERVATIONS)
                .document(reservationId)
                .get().await()
            Log.d(
                TAG,
                "getReservationById with id: $reservationId: found=${result.exists()}"
            )
            if (!result.exists()) // Reservation Id must be unique and existing
                UiState.Failure(null)
            val reservation = result.toObject(Reservation::class.java)!!
            UiState.Success(reservation)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error while performing getReservationById with id: $reservationId: ${e.message}",
                e
            )
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun getUserReservationAt(
        userId: String,
        date: String,
        time: String
    ): UiState<Reservation?> {
        return try {
            Log.d(TAG, "getUserReservationAt with user id: $userId, date: $date and time: $time")
            val result = db.collection(FirestoreCollections.RESERVATIONS)
                .whereEqualTo("date", date)
                .whereEqualTo("time", time)
                .where(
                    Filter.or(
                        Filter.equalTo("userId", userId),
                        Filter.arrayContains("participants", userId)
                    )
                )
                .get().await()
            Log.d(
                TAG,
                "getUserReservationAt with user id: $userId, date: $date and time: $time: ${result.documents.size} result"
            )
            val reservation =
                if (result.isEmpty) null else result.documents.first()
                    .toObject(Reservation::class.java)!!
            UiState.Success(reservation)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error while performing getUserReservationAt with user id: $userId, date: $date and time: $time: ${e.message}",
                e
            )
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun getReservationsAt(date: String, time: String): UiState<List<Reservation>> {
        return try {
            Log.d(TAG, "getReservationsAt with date: $date and time: $time")
            val result = db.collection(FirestoreCollections.RESERVATIONS)
                .whereEqualTo("date", date)
                .whereEqualTo("time", time)
                .get().await()
            Log.d(
                TAG,
                "getReservationsAt with date: $date and time: $time: ${result.documents.size} result"
            )
            val reservations = arrayListOf<Reservation>()
            for (document in result) {
                println(document)
                val reservation = document.toObject(Reservation::class.java)
                reservations.add(reservation)
            }
            UiState.Success(reservations)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error while performing getReservationsAt with date: $date and time: $time: ${e.message}",
                e
            )
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun getCourtReservationAt(
        courtId: String,
        date: String,
        time: String
    ): UiState<Reservation?> {
        return try {
            Log.d(TAG, "getCourtReservationAt with court id: $courtId, date: $date and time: $time")
            val result = db.collection(FirestoreCollections.RESERVATIONS)
                .whereEqualTo("courtId", courtId)
                .whereEqualTo("date", date)
                .whereEqualTo("time", time)
                .get().await()
            Log.d(
                TAG,
                "getCourtReservationAt with court id: $courtId, date: $date and time: $time: ${result.documents.size} result"
            )
            val reservation =
                if (result.isEmpty) null else result.documents.first()
                    .toObject(Reservation::class.java)!!
            UiState.Success(reservation)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error while performing getCourtReservationAt with court id: $courtId, date: $date and time: $time: ${e.message}",
                e
            )
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun saveReservation(reservation: Reservation): UiState<Unit> {
        return try {
            if (reservation.id != "") // Must be set before saveReservation
                UiState.Failure(null)
            val reservationRef = db.collection(FirestoreCollections.RESERVATIONS)
                .document(reservation.id)
            db.runTransaction { transaction ->
                val snapshot = transaction[reservationRef]
                if (snapshot.exists()) {
                    throw IllegalStateException("Unfortunately, the court is no longer available for reservation")
                }
                transaction[reservationRef] = reservation
            }.await()
            Log.d(
                TAG,
                "Reservation document added to Firestore collection with ID ${reservation.id}"
            )
            UiState.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error while performing saveReservation ${reservation.id}: ${e.message}", e)
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun updateReservation(
        reservationId: String,
        updatedReservation: Reservation
    ): UiState<Unit> {
        return try {
            if (updatedReservation.id != "") // Must be set before updateReservation
                UiState.Failure(null)
            Log.d(TAG, "Performing updateReservation of reservation with id: $reservationId")
            if (reservationId == updatedReservation.id) {
                // Just update the reservation document
                db.collection(FirestoreCollections.RESERVATIONS)
                    .document(reservationId)
                    .set(updatedReservation).await()
                Log.d(
                    TAG,
                    "updateReservation of reservation with id: $reservationId completed successfully"
                )
                return UiState.Success(Unit)
            }
            // Otherwise add the new document and delete the old one
            val oldReservationRef = db.collection(FirestoreCollections.RESERVATIONS)
                .document(reservationId)
            val newReservationRef = db.collection(FirestoreCollections.RESERVATIONS)
                .document(updatedReservation.id)
            db.runTransaction { transaction ->
                val newSnapshot = transaction[newReservationRef]
                if (newSnapshot.exists()) {
                    throw IllegalStateException("Unfortunately, the court is not available at this date and time")
                }
                transaction[newReservationRef] = updatedReservation
                transaction.delete(oldReservationRef)
            }.await()
            Log.d(
                TAG,
                "updateReservation of reservation with id: $reservationId completed successfully by deleting the old document and creating the new one"
            )
            UiState.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error while performing updateReservation $reservationId: ${e.message}", e)
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun deleteReservation(reservationId: String): UiState<Unit> {
        return try {
            db.collection(FirestoreCollections.RESERVATIONS)
                .document(reservationId)
                .delete().await()
            Log.d(
                TAG,
                "Reservation document with ID $reservationId deleted in Firestore collection"
            )
            UiState.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error while performing deleteReservation $reservationId: ${e.message}", e)
            UiState.Failure(e.localizedMessage)
        }
    }

    companion object {
        private const val TAG = "ReservationRepository"
    }

}
