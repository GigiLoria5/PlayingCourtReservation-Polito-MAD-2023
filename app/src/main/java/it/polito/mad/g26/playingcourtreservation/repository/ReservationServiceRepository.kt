package it.polito.mad.g26.playingcourtreservation.repository

import android.app.Application
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.database.CourtReservationDatabase
import it.polito.mad.g26.playingcourtreservation.model.*

class ReservationServiceRepository(application: Application) {
    private val reservationServiceDao =
        CourtReservationDatabase.getDatabase(application).reservationServiceDao()

    fun add(idReservation: Int, idsServices: List<Int>) {
        idsServices.forEach { idService ->
            val reservationService = ReservationServices().also {
                it.idReservation = idReservation
                it.idService = idService
            }
            reservationServiceDao.addReservationService(reservationService)
        }
    }

    fun getAllReservationServices(): LiveData<List<ReservationServices>> =
        reservationServiceDao.findAllReservationServices()

}