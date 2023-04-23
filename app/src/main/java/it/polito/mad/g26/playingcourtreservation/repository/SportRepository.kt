package it.polito.mad.g26.playingcourtreservation.repository

import android.app.Application
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.database.CourtReservationDatabase
import it.polito.mad.g26.playingcourtreservation.model.*

class SportRepository(application: Application) {
    private val sportDao = CourtReservationDatabase.getDatabase(application).sportDao()

    fun getAllSports(): LiveData<List<Sport>> = sportDao.findAllSports()
}