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
                .whereIn("courtId", courtsIds)
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
            Log.e(TAG, "Error while performing $courtId: ${e.message}", e)
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun getUserReservationAt(
        userId: String,
        date: String,
        time: String
    ): UiState<Reservation?> {
        return try {
            Log.d(TAG, "getUserReservationAt -> user id: $userId, date: $date and time: $time")
            val result = db.collection(FirestoreCollections.RESERVATIONS)
                .whereEqualTo("userId", userId)
                .whereEqualTo("date", date)
                .whereEqualTo("time", time)
                .get().await()
            Log.d(TAG, "getUserReservationAt: ${result.documents.size} result")
            val reservation =
                if (result.isEmpty) null else result.documents[0].toObject(Reservation::class.java)!!
            UiState.Success(reservation)
        } catch (e: Exception) {
            Log.e(TAG, "Error while performing getUserReservationAt: ${e.message}", e)
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun getCourtReservationAt(
        courtId: String,
        date: String,
        time: String
    ): UiState<Reservation?> {
        return try {
            Log.d(TAG, "getCourtReservationAt -> court id: $courtId, date: $date and time: $time")
            val result = db.collection(FirestoreCollections.RESERVATIONS)
                .whereEqualTo("courtId", courtId)
                .whereEqualTo("date", date)
                .whereEqualTo("time", time)
                .get().await()
            Log.d(TAG, "getCourtReservationAt: ${result.documents.size} result")
            val reservation =
                if (result.isEmpty) null else result.documents[0].toObject(Reservation::class.java)!!
            UiState.Success(reservation)
        } catch (e: Exception) {
            Log.e(TAG, "Error while performing getCourtReservationAt: ${e.message}", e)
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun saveReservation(reservation: Reservation): UiState<Unit> {
        return try {
            assert(reservation.id != "") // Must be set before saveReservation
            db.collection(FirestoreCollections.RESERVATIONS)
                .document(reservation.id)
                .set(reservation).await()
            UiState.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error while performing saveReservation: ${e.message}", e)
            UiState.Failure(e.localizedMessage)
        }
    }

    companion object {
        private const val TAG = "ReservationRepository"
    }

}
