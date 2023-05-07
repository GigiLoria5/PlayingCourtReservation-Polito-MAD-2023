package it.polito.mad.g26.playingcourtreservation.repository

import android.app.Application
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.database.CourtReservationDatabase
import it.polito.mad.g26.playingcourtreservation.model.Court

class CourtRepository(application: Application) {
    private val courtDao = CourtReservationDatabase.getDatabase(application).courtDao()

    fun getAllCourt(): LiveData<List<Court>> = courtDao.findAllCourt()
}