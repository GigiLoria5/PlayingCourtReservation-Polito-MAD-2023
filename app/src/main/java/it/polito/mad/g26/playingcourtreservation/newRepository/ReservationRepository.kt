package it.polito.mad.g26.playingcourtreservation.newRepository

import it.polito.mad.g26.playingcourtreservation.newModel.Reservation
import it.polito.mad.g26.playingcourtreservation.newModel.Review
import it.polito.mad.g26.playingcourtreservation.newModel.SportCenter
import it.polito.mad.g26.playingcourtreservation.util.UiState

interface ReservationRepository {
    suspend fun getAllSportCenterReviews(sportCenter: SportCenter): UiState<List<Review>>

    suspend fun getCourtReviews(courtId: String): UiState<List<Review>>

    suspend fun getUserReservationAt(userId: String, date: String, time: String): UiState<String?>

    suspend fun getCourtReservationAt(
        courtId: String,
        date: String,
        time: String
    ): UiState<Reservation?>
}