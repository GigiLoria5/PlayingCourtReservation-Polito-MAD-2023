package it.polito.mad.g26.playingcourtreservation.repository

import android.app.Application
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.database.CourtReservationDatabase
import it.polito.mad.g26.playingcourtreservation.model.ReservationWithDetails

class ReservationWithDetailsRepository(application: Application) {
    private val reservationWithDetailsDaoDao =
        CourtReservationDatabase.getDatabase(application).reservationWithDetailsDao()

    fun reservationsWithDetails(): LiveData<List<ReservationWithDetails>> =
        reservationWithDetailsDaoDao.getReservationsWithDetails()

    fun reservationWithDetails(id: Int): LiveData<ReservationWithDetails> =
        reservationWithDetailsDaoDao.getReservationWithDetailsById(id)
}
