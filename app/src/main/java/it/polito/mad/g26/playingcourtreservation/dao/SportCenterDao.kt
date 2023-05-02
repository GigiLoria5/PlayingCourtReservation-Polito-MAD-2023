package it.polito.mad.g26.playingcourtreservation.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import it.polito.mad.g26.playingcourtreservation.model.*

@Dao
interface SportCenterDao {

    @Query("SELECT * from sport_center")
    fun findAllSportCenters(): LiveData<List<SportCenter>>

    @Query("SELECT DISTINCT city FROM sport_center WHERE city LIKE :cityNameStartingWith")
    fun findCities(cityNameStartingWith: String): LiveData<List<String>>
}