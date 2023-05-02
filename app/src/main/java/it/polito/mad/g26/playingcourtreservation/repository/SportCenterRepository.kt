package it.polito.mad.g26.playingcourtreservation.repository

import android.app.Application
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.database.CourtReservationDatabase
import it.polito.mad.g26.playingcourtreservation.model.*

class SportCenterRepository(application: Application) {
    private val sportCenterDao = CourtReservationDatabase.getDatabase(application).sportCenterDao()

    fun findAllSportCenters(): LiveData<List<SportCenter>> = sportCenterDao.findAllSportCenters()

    fun findCities(cityNameStartingWith: String): LiveData<List<String>> {
        println(cityNameStartingWith)
        return sportCenterDao.findCities(cityNameStartingWith)
    }
}