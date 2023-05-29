package it.polito.mad.g26.playingcourtreservation.newRepository.impl

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.g26.playingcourtreservation.newModel.Reservation
import it.polito.mad.g26.playingcourtreservation.newModel.Review
import it.polito.mad.g26.playingcourtreservation.newModel.SportCenter
import it.polito.mad.g26.playingcourtreservation.newRepository.ReservationRepository
import it.polito.mad.g26.playingcourtreservation.util.FirestoreCollections
import it.polito.mad.g26.playingcourtreservation.util.UiState
import it.polito.mad.g26.playingcourtreservation.util.await
import javax.inject.Inject

class ReservationRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
) : ReservationRepository {

    override suspend fun getAllSportCenterReviews(sportCenter: SportCenter): UiState<List<Review>> {
        return try {
            val courtsIds = sportCenter.courts.map { it.id }
            Log.d(
                TAG,
                "Performing getAllSportCenterReviews for the sportCenter ${sportCenter.id} with the following courts: ${sportCenter.courts}"
            )
            val result = db.collection(FirestoreCollections.RESERVATIONS)
                .whereIn("idCourt", courtsIds)
                .get().await()
            Log.d(TAG, "getAllSportCenterReviews: ${result.documents.size} results")
            val reviews = arrayListOf<Review>()
            for (document in result) {
                val reservation = document.toObject(Reservation::class.java)
                reservation.id = document.id
                reservation.reviews.forEach { reviews.add(it) }
            }
            UiState.Success(reviews)
        } catch (e: Exception) {
            Log.e(TAG, "Error while performing getAllSportCenterReviews: ${e.message}", e)
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun getUserReservationAt(
        userId: String,
        date: String,
        time: String
    ): UiState<String?> {
        return try {
            Log.d(TAG, "getUserReservationAt -> user id: $userId, date: $date and time: $time")
            val result = db.collection(FirestoreCollections.RESERVATIONS)
                .whereEqualTo("userId", userId)
                .whereEqualTo("date", date)
                .whereEqualTo("time", time)
                .get().await()
            Log.d(TAG, "getUserReservationAt: ${result.documents.size} result")
            val reservationId =
                if (result.isEmpty) null else result.documents[0].toObject(Reservation::class.java)!!.id
            UiState.Success(reservationId)
        } catch (e: Exception) {
            Log.e(TAG, "Error while performing getUserReservationAt: ${e.message}", e)
            UiState.Failure(e.localizedMessage)
        }
    }

    companion object {
        private const val TAG = "ReservationRepository"
    }

}
