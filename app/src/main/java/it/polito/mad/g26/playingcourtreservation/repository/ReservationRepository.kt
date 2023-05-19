package it.polito.mad.g26.playingcourtreservation.repository

import android.app.Application
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.database.CourtReservationDatabase
import it.polito.mad.g26.playingcourtreservation.model.*

class ReservationRepository(application: Application) {
    private val reservationDao = CourtReservationDatabase.getDatabase(application).reservationDao()

    fun reservations(): LiveData<List<Reservation>> = reservationDao.findAll()
    fun add(idUser: Int, idCourt: Int, date: String, time: String, amount: Float): Long {
        val reservation = Reservation().also {
            it.idUser = idUser
            it.idCourt = idCourt
            it.date = date
            it.time = time
            it.amount = amount
        }
        return reservationDao.addReservation(reservation)
    }

    fun filteredReservations(
        date: String,
        hour: String,
        courtsIdList: List<Int>
    ): LiveData<List<Reservation>> =
        reservationDao.findFiltered(date, hour, courtsIdList)

    fun reservationIdByUserIdAndDateAndHour(userId: Int, date: String, hour: String): LiveData<Int?> =
        reservationDao.findReservationIdByUserIdAndDateAndHour(userId, date, hour)
}