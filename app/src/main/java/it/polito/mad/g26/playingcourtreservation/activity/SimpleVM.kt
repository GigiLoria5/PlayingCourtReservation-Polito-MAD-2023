package it.polito.mad.g26.playingcourtreservation.activity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.model.*
import it.polito.mad.g26.playingcourtreservation.repository.CourtReservationRepository

class SimpleVM(application: Application): AndroidViewModel(application) {
    val repo = CourtReservationRepository(application)

    val courtList: LiveData<List<Court>> = repo.getAllCourt()
    val reservationList: LiveData<List<Reservation>> = repo.getAllReservation()
    val reservationServicesList: LiveData<List<ReservationServices>> = repo.getAllReservationServices()
    val serviceList: LiveData<List<Service>> = repo.getAllServices()
    val sportsList: LiveData<List<Sport>> = repo.getAllSports()
    val sportCenterList: LiveData<List<SportCenter>> = repo.getAllSportCenters()
    val sportCenterServiceList: LiveData<List<SportCenterServices>> = repo.getAllSportCenterServices()

}