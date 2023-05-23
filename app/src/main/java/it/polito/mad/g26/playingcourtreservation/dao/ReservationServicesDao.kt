package it.polito.mad.g26.playingcourtreservation.dao

import androidx.room.Dao
import androidx.room.Insert
import it.polito.mad.g26.playingcourtreservation.model.*

@Dao
fun interface ReservationServicesDao {

    @Insert
    fun addReservationService(reservationService: ReservationServices)
}