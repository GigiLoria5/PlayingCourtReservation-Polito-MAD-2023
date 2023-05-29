package it.polito.mad.g26.playingcourtreservation.newRepository

import it.polito.mad.g26.playingcourtreservation.newModel.Review
import it.polito.mad.g26.playingcourtreservation.newModel.SportCenter
import it.polito.mad.g26.playingcourtreservation.util.UiState

interface ReservationRepository {
    suspend fun getAllSportCenterReviews(sportCenter: SportCenter): UiState<List<Review>>
    suspend fun getUserReservationAt(userId: String, date: String, time: String): UiState<String?>
}