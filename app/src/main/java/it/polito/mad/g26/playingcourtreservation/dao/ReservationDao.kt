package it.polito.mad.g26.playingcourtreservation.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import it.polito.mad.g26.playingcourtreservation.model.*

@Dao
interface ReservationDao {

    @Insert
    fun addReservation(reservation: Reservation): Long

    @Query(
        "SELECT reservation.id , reservation.id_user , reservation.id_court , reservation.date , reservation.time , reservation.amount " +
                "FROM reservation,court,sport_center " +
                "WHERE reservation.id_court=court.id " +
                "AND court.id_sport_center=sport_center.id " +
                "AND reservation.date=:date " +
                "AND reservation.time=:hour " +
                "AND court.id IN (:courtsIdList)"
    )
    fun findFiltered(
        date: String,
        hour: String,
        courtsIdList: List<Int>
    ): LiveData<List<Reservation>>

    @Query(
        "SELECT reservation.id " +
                "FROM reservation " +
                "WHERE reservation.id_user=:userId " +
                "AND reservation.date=:date " +
                "AND reservation.time=:hour "
    )
    fun findReservationIdByUserIdAndDateAndHour(
        userId: Int,
        date: String,
        hour: String
    ): LiveData<Int?>

    @Transaction
    @Query("DELETE FROM reservation WHERE id= :id")
    fun deleteReservationById(id: Int)

    @Query("UPDATE Reservation SET date=:date, time= :hour, amount= :amount WHERE id= :id")
    fun updateReservationDateAndHourAndAmount(date: String, hour: String, id: Int, amount: Float)

    @Query("SELECT id FROM reservation WHERE time=:hour AND date=:date ")
    fun findDataAndHour(date: String, hour: String): LiveData<Int?>

}