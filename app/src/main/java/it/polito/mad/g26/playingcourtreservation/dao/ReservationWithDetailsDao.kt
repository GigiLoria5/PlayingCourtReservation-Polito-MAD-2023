package it.polito.mad.g26.playingcourtreservation.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import it.polito.mad.g26.playingcourtreservation.model.ReservationWithDetails

@Dao
interface ReservationWithDetailsDao {

    @Transaction
    @Query("SELECT * FROM reservation ORDER BY time ASC")
    fun getReservationsWithDetails(): LiveData<List<ReservationWithDetails>>

    @Transaction
    @Query("SELECT * FROM reservation WHERE id = :reservationId")
    fun getReservationWithDetailsById(reservationId: Int): LiveData<ReservationWithDetails>

    @Query("DELETE FROM reservation_services WHERE id_reservation = :id")
    fun deleteServices(id: Int)

}
