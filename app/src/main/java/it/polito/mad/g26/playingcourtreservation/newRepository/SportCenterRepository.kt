package it.polito.mad.g26.playingcourtreservation.newRepository

import it.polito.mad.g26.playingcourtreservation.newModel.SportCenter
import it.polito.mad.g26.playingcourtreservation.util.UiState

interface SportCenterRepository {
    suspend fun getSportCenters(): UiState<List<SportCenter>>
    suspend fun getAllSportCentersCities(): UiState<List<String>>
    suspend fun getFilteredSportCentersCities(cityNamePrefix: String): UiState<List<String>>
}