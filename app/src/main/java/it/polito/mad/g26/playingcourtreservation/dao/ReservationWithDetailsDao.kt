package it.polito.mad.g26.playingcourtreservation.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import it.polito.mad.g26.playingcourtreservation.model.ReservationWithDetails

@Dao
fun interface ReservationWithDetailsDao {

    @Transaction
    @Query("SELECT * FROM reservation")
    fun getReservationsWithDetails(): LiveData<List<ReservationWithDetails>>

}
