package it.polito.mad.g26.playingcourtreservation.repository

import android.app.Application
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.database.CourtReservationDatabase
import it.polito.mad.g26.playingcourtreservation.model.ReservationWithDetails
import it.polito.mad.g26.playingcourtreservation.model.Service
import it.polito.mad.g26.playingcourtreservation.model.SportCenterServices

class ReservationWithDetailsRepository(application: Application) {
    private val reservationWithDetailsDaoDao =
        CourtReservationDatabase.getDatabase(application).reservationWithDetailsDao()
    private val repoSportCenterServices= CourtReservationDatabase.getDatabase(application).sportCenterServiceDao()
    private val repoService= CourtReservationDatabase.getDatabase(application).serviceDao()

    fun reservationsWithDetails(): LiveData<List<ReservationWithDetails>> =
        reservationWithDetailsDaoDao.getReservationsWithDetails()

    fun reservationWithDetails(id: Int): LiveData<ReservationWithDetails> =
        reservationWithDetailsDaoDao.getReservationWithDetailsById(id)

    fun getAllServicesWithFee(id:Int) : LiveData<List<SportCenterServices>> =
    repoSportCenterServices.getAllServicesWithFee(id)

    fun getAllServices() : LiveData<List<Service>> =
    repoService.findAll()
}
