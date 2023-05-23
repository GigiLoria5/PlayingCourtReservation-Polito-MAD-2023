package it.polito.mad.g26.playingcourtreservation.repository

import android.app.Application
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.database.CourtReservationDatabase
import it.polito.mad.g26.playingcourtreservation.model.SportCenter
import it.polito.mad.g26.playingcourtreservation.model.custom.SportCenterWithDetails

class SportCenterRepository(application: Application) {
    private val sportCenterDao = CourtReservationDatabase.getDatabase(application).sportCenterDao()
    fun findAllCities(): LiveData<List<String>> = sportCenterDao.findAllCities()
    fun filteredCities(cityNameStartingWith: String): LiveData<List<String>> =
        sportCenterDao.findFilteredCities(cityNameStartingWith)

    fun filterSportCenters(
        city: String,
        hour: String
    ): LiveData<List<SportCenterWithDetails>> = sportCenterDao.findFiltered(city, hour)

    fun getSportCenter(sportCenterId: Int): LiveData<SportCenter> = sportCenterDao.getSportCenter(sportCenterId)
}