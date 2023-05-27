package it.polito.mad.g26.playingcourtreservation.newRepository

import it.polito.mad.g26.playingcourtreservation.newModel.SportCenter
import it.polito.mad.g26.playingcourtreservation.util.UiState

interface SportCenterRepository {
    fun getSportCenters(): UiState<List<SportCenter>>

    fun getSportCentersCities(): UiState<List<String>>
}