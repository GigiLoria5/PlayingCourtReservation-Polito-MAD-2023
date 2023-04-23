package it.polito.mad.g26.playingcourtreservation.repository

import android.app.Application
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.database.CourtReservationDatabase
import it.polito.mad.g26.playingcourtreservation.model.*

class SportCenterServicesRepository(application: Application) {
    private val sportCenterServicesDao = CourtReservationDatabase.getDatabase(application).sportCenterServiceDao()

    fun getAllSportCenterServices(): LiveData<List<SportCenterServices>> = sportCenterServicesDao.findAllSportCenterServices()

}