package it.polito.mad.g26.playingcourtreservation.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import it.polito.mad.g26.playingcourtreservation.model.*

@Dao
interface ReservationDao {

    //Reservation
    @Query("SELECT * from reservation")
    fun findAllReservation(): LiveData<List<Reservation>>

    @Query("SELECT reservation.id , reservation.id_user , reservation.id_court , reservation.date , reservation.time , reservation.amount " +
            "FROM reservation,court,sport_center " +
            "WHERE reservation.id_court=court.id " +
            "AND court.id_sport_center=sport_center.id " +
            "AND sport_center.city=:city " +
            "AND reservation.date=:date " +
            "AND reservation.time=:hour " +
            "AND court.id IN (:courtsIdList)")
    fun findFiltered(city: String, date: String, hour: String, courtsIdList: List<Int>): LiveData<List<Reservation>>


}