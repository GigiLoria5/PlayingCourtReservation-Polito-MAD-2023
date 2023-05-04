package it.polito.mad.g26.playingcourtreservation.repository

import android.app.Application
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.database.CourtReservationDatabase
import it.polito.mad.g26.playingcourtreservation.model.*

class SportCenterRepository(application: Application) {
    private val sportCenterDao = CourtReservationDatabase.getDatabase(application).sportCenterDao()

    fun sportCenters(): LiveData<List<SportCenter>> = sportCenterDao.findAll()

    fun filteredCities(cityNameStartingWith: String): LiveData<List<String>> = sportCenterDao.findFilteredCities(cityNameStartingWith)

    //x le date
    //select * from sport_center where STRFTIME('%H', open_time) < '10'
}