package it.polito.mad.g26.playingcourtreservation.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import it.polito.mad.g26.playingcourtreservation.model.*
import it.polito.mad.g26.playingcourtreservation.model.custom.SportCenterServicesCourts

@Dao
interface SportCenterDao {

    @Query("SELECT * from sport_center")
    fun findAll(): LiveData<List<SportCenter>>

    @Query("SELECT DISTINCT city FROM sport_center WHERE city LIKE :cityNameStartingWith")
    fun findFilteredCities(cityNameStartingWith: String): LiveData<List<String>>

    @Transaction
    @Query(
        "SELECT * " +
                "from sport_center " +
                "WHERE " +
                "STRFTIME('%H:%M', open_time) <= :hour AND STRFTIME('%H:%M', close_time)> :hour " +
                "AND " +
                "city=:city " +
                ""
    )
    fun findFilteredBase(city: String, hour: String): LiveData<List<SportCenterServicesCourts>>

    @Transaction
    @Query(
        "SELECT DISTINCT sport_center.id, sport_center.name, sport_center.address, sport_center.city, sport_center.longitude, sport_center.latitude, sport_center.phone_number, sport_center.open_time, sport_center.close_time " +
                "from sport_center, court " +
                "WHERE " +
                "STRFTIME('%H:%M', open_time) <= :hour AND STRFTIME('%H:%M', close_time)> :hour " +
                "AND " +
                "city=:city " +
                "AND " +
                "court.id_sport_center=sport_center.id " +
                "AND " +
                "id_sport=:sportId "
    )
    fun findFilteredSportId(
        city: String,
        hour: String,
        sportId: Int
    ): LiveData<List<SportCenterServicesCourts>>


    @Transaction
    @Query(
        "SELECT sport_center.id, sport_center.name, sport_center.address, sport_center.city, sport_center.longitude, sport_center.latitude, sport_center.phone_number, sport_center.open_time, sport_center.close_time " +
                "from sport_center, sport_center_services " +
                "WHERE " +
                "STRFTIME('%H:%M', open_time) <= :hour AND STRFTIME('%H:%M', close_time)> :hour " +
                "AND " +
                "city=:city " +
                "AND " +
                "sport_center_services.id_sport_center=sport_center.id " +
                "AND " +
                "sport_center_services.id_service IN (:services) " +
                "GROUP BY sport_center.id " +
                "HAVING count(*)== :servicesSize"
    )
    fun findFilteredServices(
        city: String,
        hour: String,
        services: Set<Int>,
        servicesSize: Int
    ): LiveData<List<SportCenterServicesCourts>>

    @Transaction
    @Query(
        "SELECT sport_center.id, sport_center.name, sport_center.address, sport_center.city, sport_center.longitude, sport_center.latitude, sport_center.phone_number, sport_center.open_time, sport_center.close_time " +
                "from sport_center, sport_center_services, court " +
                "WHERE " +
                "STRFTIME('%H:%M', open_time) <= :hour AND STRFTIME('%H:%M', close_time)> :hour " +
                "AND " +
                "city=:city " +
                "AND " +
                "sport_center_services.id_sport_center=sport_center.id " +
                "AND " +
                "sport_center_services.id_service IN (:services) " +
                "AND " +
                "court.id_sport_center=sport_center.id " +
                "AND " +
                "id_sport=:sportId " +
                "GROUP BY sport_center.id " +
                "HAVING count(*)== :servicesSize"
    )
    fun findFilteredServicesAndSport(
        city: String,
        hour: String,
        services: Set<Int>,
        servicesSize: Int,
        sportId: Int
    ): LiveData<List<SportCenterServicesCourts>>
}