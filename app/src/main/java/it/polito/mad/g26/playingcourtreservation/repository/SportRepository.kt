package it.polito.mad.g26.playingcourtreservation.repository

import android.app.Application
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.database.CourtReservationDatabase
import it.polito.mad.g26.playingcourtreservation.model.Sport

class SportRepository(application: Application) {
    private val sportDao = CourtReservationDatabase.getDatabase(application).sportDao()

    fun sports(): LiveData<List<Sport>> = sportDao.findAll()
}