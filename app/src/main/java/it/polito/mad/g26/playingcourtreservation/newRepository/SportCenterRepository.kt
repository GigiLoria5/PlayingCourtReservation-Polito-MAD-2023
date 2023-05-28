package it.polito.mad.g26.playingcourtreservation.newRepository

import it.polito.mad.g26.playingcourtreservation.newModel.SportCenter
import it.polito.mad.g26.playingcourtreservation.util.UiState

interface SportCenterRepository {
    suspend fun getSportCenters(): UiState<List<SportCenter>>
    suspend fun getSportCentersCities(): UiState<List<String>>
}