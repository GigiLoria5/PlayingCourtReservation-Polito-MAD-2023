package it.polito.mad.g26.playingcourtreservation.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import it.polito.mad.g26.playingcourtreservation.model.*

@Dao
interface ReservationServicesDao {

    @Insert
    fun addReservationService(reservationService: ReservationServices)

    @Query("SELECT * from reservation_services")
    fun findAllReservationServices(): LiveData<List<ReservationServices>>

}