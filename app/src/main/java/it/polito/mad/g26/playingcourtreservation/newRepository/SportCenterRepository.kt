package it.polito.mad.g26.playingcourtreservation.newRepository

import it.polito.mad.g26.playingcourtreservation.newModel.SportCenter

interface SportCenterRepository {
    fun getSportCenters(): List<SportCenter>

    fun getSportCentersCities(): List<String>
}