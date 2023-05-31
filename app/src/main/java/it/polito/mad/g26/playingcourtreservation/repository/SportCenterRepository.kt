package it.polito.mad.g26.playingcourtreservation.repository

import it.polito.mad.g26.playingcourtreservation.model.Court
import it.polito.mad.g26.playingcourtreservation.model.Service
import it.polito.mad.g26.playingcourtreservation.model.SportCenter
import it.polito.mad.g26.playingcourtreservation.util.UiState

interface SportCenterRepository {
    suspend fun getAllSportCenters(): UiState<List<SportCenter>>

    suspend fun getSportCenterById(sportCenterId: String): UiState<SportCenter>

    suspend fun getAllSportCentersCities(): UiState<List<String>>

    suspend fun getFilteredSportCentersCities(cityNamePrefix: String): UiState<List<String>>

    suspend fun getAllSportCenterCourts(sportCenterId: String): UiState<List<Court>>

    suspend fun getAllSportCenterCourtsBySport(
        sportCenterId: String,
        sportName: String
    ): UiState<List<Court>>

    suspend fun getSportCenterServices(sportCenterId: String): UiState<List<Service>>
}