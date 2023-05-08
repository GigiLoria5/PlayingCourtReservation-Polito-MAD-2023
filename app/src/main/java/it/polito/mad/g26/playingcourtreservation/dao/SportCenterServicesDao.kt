package it.polito.mad.g26.playingcourtreservation.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import it.polito.mad.g26.playingcourtreservation.model.*

@Dao
interface SportCenterServicesDao {

    @Query("SELECT * from sport_center_services")
    fun findAllSportCenterServices(): LiveData<List<SportCenterServices>>

    @Transaction
    @Query("SELECT * FROM sport_center_services WHERE id_sport_center = :id_sport_center")
    fun getAllServicesWithFee(id_sport_center : Int): LiveData<List<SportCenterServices>>


}