package it.polito.mad.g26.playingcourtreservation.repository

import android.app.Application
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.database.CourtReservationDatabase
import it.polito.mad.g26.playingcourtreservation.model.*

class SportCenterRepository(application: Application) {
    private val sportCenterDao = CourtReservationDatabase.getDatabase(application).sportCenterDao()

    fun sportCenters(): LiveData<List<SportCenter>> = sportCenterDao.findAll()

    fun filteredCities(cityNameStartingWith: String): LiveData<List<String>> =
        sportCenterDao.findFilteredCities(cityNameStartingWith)

    fun filteredSportCentersBase(city: String, hour: String): LiveData<List<SportCenter>> =
        sportCenterDao.findFilteredBase(city, hour)

    fun filteredSportCentersSportId(
        city: String,
        hour: String,
        sportId: Int
    ): LiveData<List<SportCenter>> =
        sportCenterDao.findFilteredSportId(city, hour, sportId)

    fun filteredSportCentersServices(
        city: String,
        hour: String,
        services: Set<Int>
    ): LiveData<List<SportCenter>> =
        sportCenterDao.findFilteredServices(city, hour, services, services.size)

    fun filteredSportCentersServicesAndSport(
        city: String,
        hour: String,
        services: Set<Int>,
        sportId: Int
    ): LiveData<List<SportCenter>> =
        sportCenterDao.findFilteredServicesAndSport(city, hour, services, services.size, sportId)
}