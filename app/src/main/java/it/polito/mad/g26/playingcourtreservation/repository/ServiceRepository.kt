package it.polito.mad.g26.playingcourtreservation.repository

import android.app.Application
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.database.CourtReservationDatabase
import it.polito.mad.g26.playingcourtreservation.model.*

class ServiceRepository(application: Application) {
    private val serviceDao = CourtReservationDatabase.getDatabase(application).serviceDao()

    fun getAllServices(): LiveData<List<Service>> = serviceDao.findAllServices()
}