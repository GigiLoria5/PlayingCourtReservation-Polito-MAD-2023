package it.polito.mad.g26.playingcourtreservation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.model.*
import it.polito.mad.g26.playingcourtreservation.repository.ReservationServiceRepository

class ReservationXServiceVM(application: Application): AndroidViewModel(application) {
    val repo = ReservationServiceRepository(application)

    val reservationServicesList: LiveData<List<ReservationServices>> = repo.getAllReservationServices()

    fun addService(s:MutableList<Service>, o : Service) {
        if(o in s) s.add(o)
    }

    fun removeService(s:MutableList<Service>, o : Service){
        if(o in s) s.remove(o)
    }

}