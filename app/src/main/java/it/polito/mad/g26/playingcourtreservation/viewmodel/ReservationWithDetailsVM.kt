package it.polito.mad.g26.playingcourtreservation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.model.ReservationWithDetails
import it.polito.mad.g26.playingcourtreservation.repository.ReservationWithDetailsRepository

class ReservationWithDetailsVM(application: Application) : AndroidViewModel(application) {
    private val repo = ReservationWithDetailsRepository(application)

    val reservationWithDetails: LiveData<List<ReservationWithDetails>> =
        repo.reservationsWithDetails()

    fun getReservationWithDetailsById(id: Int): LiveData<ReservationWithDetails> =
        repo.reservationWithDetails(id)

}
