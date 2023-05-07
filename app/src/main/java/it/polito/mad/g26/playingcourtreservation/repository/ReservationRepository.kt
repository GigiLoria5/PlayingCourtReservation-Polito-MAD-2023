package it.polito.mad.g26.playingcourtreservation.repository

import android.app.Application
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.database.CourtReservationDatabase
import it.polito.mad.g26.playingcourtreservation.model.*

class ReservationRepository(application: Application) {
    private val reservationDao = CourtReservationDatabase.getDatabase(application).reservationDao()

    fun getAllReservation(): LiveData<List<Reservation>> = reservationDao.findAllReservation()

    fun filteredReservations(date: String, hour: String, courtsIdList: List<Int>): LiveData<List<Reservation>> =
        reservationDao.findFiltered( date, hour,courtsIdList)

    fun myReservationId(myUserId: Int,date: String, hour: String): LiveData<Int?> =
        reservationDao.findMyReservationId( myUserId,date, hour)
}