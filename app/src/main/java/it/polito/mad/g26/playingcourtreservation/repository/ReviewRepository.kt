package it.polito.mad.g26.playingcourtreservation.repository

import android.app.Application
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.database.CourtReservationDatabase
import it.polito.mad.g26.playingcourtreservation.model.*

class ReviewRepository(application: Application) {
    private val reviewDao = CourtReservationDatabase.getDatabase(application).reviewDao()

    fun reviews(): LiveData<List<Review>> = reviewDao.findAll()
}