package it.polito.mad.g26.playingcourtreservation.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import it.polito.mad.g26.playingcourtreservation.model.ReservationWithDetails

@Dao
interface ReservationWithDetailsDao {

    @Transaction
    @Query("SELECT * FROM reservation r, court c, sport_center sc, sport s, service se, reservation_services rs WHERE r.id_court = c.id AND c.id_sport_center = sc.id AND c.id_sport = s.id AND rs.id_reservation = r.id AND rs.id_service = se.id")
    fun getReservationsWithDetails(): LiveData<List<ReservationWithDetails>>

}
