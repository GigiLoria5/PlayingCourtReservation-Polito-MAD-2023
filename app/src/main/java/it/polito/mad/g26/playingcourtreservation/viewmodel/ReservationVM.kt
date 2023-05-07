package it.polito.mad.g26.playingcourtreservation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.model.Reservation
import it.polito.mad.g26.playingcourtreservation.repository.ReservationRepository

class ReservationVM(application: Application) : AndroidViewModel(application) {
    private val repo = ReservationRepository(application)

    val reservations: LiveData<List<Reservation>> = repo.reservations()
}
