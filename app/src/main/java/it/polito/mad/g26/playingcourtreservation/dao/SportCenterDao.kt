package it.polito.mad.g26.playingcourtreservation.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import it.polito.mad.g26.playingcourtreservation.model.*
import it.polito.mad.g26.playingcourtreservation.model.custom.SportCenterWithDetails

@Dao
interface SportCenterDao {

    @Query("SELECT * from sport_center")
    fun findAll(): LiveData<List<SportCenter>>

    @Query("SELECT DISTINCT city FROM sport_center ORDER BY city ASC")
    fun findAllCities(): LiveData<List<String>>

    @Query("SELECT DISTINCT city FROM sport_center WHERE city LIKE :cityNameStartingWith ORDER BY city ASC")
    fun findFilteredCities(cityNameStartingWith: String): LiveData<List<String>>

    @Transaction
    @Query(
        "SELECT * " +
                "from sport_center " +
                "WHERE " +
                "STRFTIME('%H:%M', open_time) <= :hour AND STRFTIME('%H:%M', close_time)> :hour " +
                "AND " +
                "city=:city "
    )
    fun findFiltered(city: String, hour: String): LiveData<List<SportCenterWithDetails>>

    @Transaction
    @Query(
        "SELECT * " +
                "from sport_center " +
                "WHERE " +
                "id=:sportCenterId "
    )
    fun findById(sportCenterId: Int): LiveData<SportCenterWithDetails>

}
