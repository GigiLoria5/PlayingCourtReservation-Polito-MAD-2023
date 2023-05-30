package it.polito.mad.g26.playingcourtreservation.newRepository

import it.polito.mad.g26.playingcourtreservation.newModel.Reservation
import it.polito.mad.g26.playingcourtreservation.newModel.Review
import it.polito.mad.g26.playingcourtreservation.newModel.SportCenter
import it.polito.mad.g26.playingcourtreservation.util.UiState

interface ReservationRepository {
    suspend fun getAllSportCenterReviews(sportCenter: SportCenter): UiState<List<Review>>

    suspend fun getCourtReviews(courtId: String): UiState<List<Review>>

    suspend fun deleteUserReview(reservationId: String, userId: String): UiState<Unit>

    suspend fun getUserReservations(userId: String): UiState<List<Reservation>>

    suspend fun getReservationById(reservationId: String): UiState<Reservation>

    suspend fun getUserReservationAt(
        userId: String,
        date: String,
        time: String
    ): UiState<Reservation?>

    suspend fun getCourtReservationAt(
        courtId: String,
        date: String,
        time: String
    ): UiState<Reservation?>

    suspend fun saveReservation(reservation: Reservation): UiState<Unit>

    suspend fun deleteReservation(reservationId: String): UiState<Unit>
}