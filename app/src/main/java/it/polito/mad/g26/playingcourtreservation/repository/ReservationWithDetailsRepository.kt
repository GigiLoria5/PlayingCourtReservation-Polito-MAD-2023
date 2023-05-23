package it.polito.mad.g26.playingcourtreservation.repository

import android.app.Application
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.database.CourtReservationDatabase
import it.polito.mad.g26.playingcourtreservation.model.ReservationWithDetails
import it.polito.mad.g26.playingcourtreservation.model.Service
import it.polito.mad.g26.playingcourtreservation.model.SportCenterServices

class ReservationWithDetailsRepository(application: Application) {
    private val reservationWithDetailsDao =
        CourtReservationDatabase.getDatabase(application).reservationWithDetailsDao()
    private val repoSportCenterServices =
        CourtReservationDatabase.getDatabase(application).sportCenterServiceDao()
    private val repoService = CourtReservationDatabase.getDatabase(application).serviceDao()
    private val reservationDao = CourtReservationDatabase.getDatabase(application).reservationDao()

    fun reservationsWithDetails(): LiveData<List<ReservationWithDetails>> =
        reservationWithDetailsDao.getReservationsWithDetails()

    fun reservationWithDetails(id: Int): LiveData<ReservationWithDetails> =
        reservationWithDetailsDao.getReservationWithDetailsById(id)

    fun getAllServicesWithFee(id: Int): LiveData<List<SportCenterServices>> =
        repoSportCenterServices.getAllServicesWithFee(id)

    fun getAllServices(): LiveData<List<Service>> =
        repoService.findAll()

    fun deleteReservationById(id: Int): Unit =
        reservationDao.deleteReservationById(id)

    fun updateDateAndHourAndAmount(date: String, hour: String, id: Int, amount: Float): Boolean {
        reservationDao.updateReservationDateAndHourAndAmount(date, hour, id, amount)
        return true
    }

    fun findDataAndHour(date: String, hour: String): LiveData<Int?> =
        reservationDao.findDataAndHour(date, hour)

    fun deleteServices(id: Int) {
        reservationWithDetailsDao.deleteServices(id)
    }

}
