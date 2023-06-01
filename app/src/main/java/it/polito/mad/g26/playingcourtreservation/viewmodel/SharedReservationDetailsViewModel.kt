package it.polito.mad.g26.playingcourtreservation.viewmodel

import androidx.lifecycle.ViewModel
import it.polito.mad.g26.playingcourtreservation.model.Court
import it.polito.mad.g26.playingcourtreservation.model.Reservation
import it.polito.mad.g26.playingcourtreservation.model.SportCenter

class SharedReservationDetailsViewModel : ViewModel() {
    var reservation = Reservation()
    var reservationSportCenter = SportCenter()
    var reservationCourt = Court()
}
