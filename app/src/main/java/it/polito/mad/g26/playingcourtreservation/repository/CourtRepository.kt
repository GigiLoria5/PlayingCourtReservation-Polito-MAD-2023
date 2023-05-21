package it.polito.mad.g26.playingcourtreservation.repository

import android.app.Application
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.database.CourtReservationDatabase
import it.polito.mad.g26.playingcourtreservation.model.Court
import it.polito.mad.g26.playingcourtreservation.model.CourtWithDetails

class CourtRepository(application: Application) {
    private val courtDao = CourtReservationDatabase.getDatabase(application).courtDao()

    fun getAllCourt(): LiveData<List<Court>> = courtDao.findAllCourt()

    fun getCourtsBySportCenterId(sportCenterId: Int): LiveData<List<CourtWithDetails>> =
        courtDao.findBySportCenterId(sportCenterId)
}