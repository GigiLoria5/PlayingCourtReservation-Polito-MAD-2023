package it.polito.mad.g26.playingcourtreservation.repository

import android.app.Application
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.database.CourtReservationDatabase
import it.polito.mad.g26.playingcourtreservation.model.*

class ServiceRepository(application: Application) {
    private val serviceDao = CourtReservationDatabase.getDatabase(application).serviceDao()
    private val sportCenterDao = CourtReservationDatabase.getDatabase(application).sportCenterDao()

    fun services(): LiveData<List<Service>> = serviceDao.findAll()

}