package it.polito.mad.g26.playingcourtreservation.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import it.polito.mad.g26.playingcourtreservation.model.*

@Dao
interface CourtReservationDao {

    //Court
    @Query("SELECT * from court")
    fun findAllCourt(): LiveData<List<Court>>

    @Insert
    fun addCourt(item: Court)

    @Query("DELETE FROM court WHERE id= :id")
    fun removeCourt(id:Int)

    @Query("DELETE FROM court")
    fun removeAllCourts()

    //Reservation
    @Query("SELECT * from reservation")
    fun findAllReservation(): LiveData<List<Reservation>>

    @Insert
    fun addReservation(item: Reservation)

    @Query("DELETE FROM reservation WHERE id= :id")
    fun removeReservation(id:Int)

    @Query("DELETE FROM reservation")
    fun removeAllReservations()

    //ReservationServices
    @Query("SELECT * from reservation_services")
    fun findAllReservationServices(): LiveData<List<ReservationServices>>

    @Insert
    fun addReservationServices(item: ReservationServices)

    @Query("DELETE FROM reservation_services WHERE id_reservation= :id_reservation AND id_service= :id_service")
    fun removeReservationServices(id_reservation:Int, id_service: Int)

    @Query("DELETE FROM reservation_services")
    fun removeAllReservationServices()

    //SportCenter
    @Query("SELECT * from sport_center")
    fun findAllSportCenters(): LiveData<List<SportCenter>>

    @Insert
    fun addSportCenter(item: SportCenter)

    @Query("DELETE FROM sport_center WHERE id= :id")
    fun removeSportCenter(id:Int)

    @Query("DELETE FROM sport_center")
    fun removeAllSportCenters()

    //SportCenterServices
    @Query("SELECT * from sport_center_services")
    fun findAllSportCenterServices(): LiveData<List<SportCenterServices>>

    @Insert
    fun addSportCenterService(item: SportCenterServices)

    @Query("DELETE FROM sport_center_services WHERE id_sport_center= :id_sport_center AND id_service= :id_service")
    fun removeSportCenterService(id_sport_center: Int, id_service: Int)

    @Query("DELETE FROM sport_center_services")
    fun removeSportCenterServices()

    //Sport
    @Query("SELECT * from sport")
    fun findAllSports(): LiveData<List<Sport>>

    @Insert
    fun addSport(item: Sport)

    @Query("DELETE FROM sport WHERE id= :id")
    fun removeSport(id:Int)

    @Query("DELETE FROM sport")
    fun removeAllSports()

    //Service
    @Query("SELECT * from service")
    fun findAllServices(): LiveData<List<Service>>

    @Insert
    fun addService(item: Service)

    @Query("DELETE FROM service WHERE id= :id")
    fun removeService(id:Int)

    @Query("DELETE FROM service")
    fun removeAllServices()
}