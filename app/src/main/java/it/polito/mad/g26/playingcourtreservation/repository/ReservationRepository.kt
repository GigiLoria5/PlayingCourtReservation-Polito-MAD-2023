package it.polito.mad.g26.playingcourtreservation.repository

import it.polito.mad.g26.playingcourtreservation.model.Reservation
import it.polito.mad.g26.playingcourtreservation.model.Review
import it.polito.mad.g26.playingcourtreservation.model.SportCenter
import it.polito.mad.g26.playingcourtreservation.util.UiState

interface ReservationRepository {
    suspend fun getAllSportCenterReviews(sportCenter: SportCenter): UiState<List<Review>>

    suspend fun getCourtReviews(courtId: String): UiState<List<Review>>

    suspend fun getReservationReviewByUserId(
        reservationId: String,
        userId: String
    ): UiState<Review?>

    suspend fun saveReview(reservationId: String, review: Review): UiState<Unit>

    suspend fun updateReview(reservationId: String, review: Review): UiState<Unit>

    suspend fun deleteUserReview(reservationId: String, userId: String): UiState<Unit>

    suspend fun getUserReservations(userId: String): UiState<List<Reservation>>

    suspend fun getReservationById(reservationId: String): UiState<Reservation>

    suspend fun getUserReservationAt(
        userId: String,
        date: String,
        time: String
    ): UiState<Reservation?>

    suspend fun getReservationsAt(
        date: String,
        time: String
    ): UiState<List<Reservation>>

    suspend fun getCourtReservationAt(
        courtId: String,
        date: String,
        time: String
    ): UiState<Reservation?>

    suspend fun saveReservation(reservation: Reservation): UiState<Unit>

    suspend fun updateReservation(
        reservationId: String,
        updatedReservation: Reservation
    ): UiState<Unit>

    suspend fun deleteReservation(reservationId: String): UiState<Unit>

    suspend fun inviteUser(reservationId: String, userId: String): UiState<Unit>

    suspend fun addParticipant(reservationId: String, userId: String): UiState<Unit>
    suspend fun removeRequester(reservationId: String, userId: String): UiState<Unit>

}