package it.polito.mad.g26.playingcourtreservation.repository

import android.app.Application
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.database.CourtReservationDatabase
import it.polito.mad.g26.playingcourtreservation.model.SportCenter
import it.polito.mad.g26.playingcourtreservation.model.custom.SportCenterWithServices

class SportCenterRepository(application: Application) {
    private val sportCenterDao = CourtReservationDatabase.getDatabase(application).sportCenterDao()

    fun sportCenters(): LiveData<List<SportCenter>> = sportCenterDao.findAll()

    fun findAllCities(): LiveData<List<String>> = sportCenterDao.findAllCities()

    fun filteredCities(cityNameStartingWith: String): LiveData<List<String>> =
        sportCenterDao.findFilteredCities(cityNameStartingWith)

    fun filterSportCenters(
        city: String,
        hour: String,
        services: Set<Int>,
        sportId: Int
    ): LiveData<List<SportCenterWithServices>> {
        return when {
            /* FILTERING BY SPORT, SERVICES AND BASE (DATE,TIME,CITY)*/
            (sportId != 0 && services.isNotEmpty()) ->
                sportCenterDao.findFilteredByServicesIdsAndSportId(
                    city,
                    hour,
                    services,
                    services.size,
                    sportId
                )
            /* FILTERING BY SPORT AND BASE (DATE,TIME,CITY)*/
            (sportId != 0) -> sportCenterDao.findFilteredBySportId(city, hour, sportId)

            /* FILTERING BY SERVICES AND BASE (DATE,TIME,CITY)*/
            (services.isNotEmpty()) -> sportCenterDao.findFilteredByServicesIds(
                city,
                hour,
                services,
                services.size
            )

            /* BASE FILTERING (DATE,TIME,CITY) */
            else -> sportCenterDao.findFilteredBase(city, hour)
        }
    }
}