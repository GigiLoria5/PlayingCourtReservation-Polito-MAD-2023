package it.polito.mad.g26.playingcourtreservation.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import it.polito.mad.g26.playingcourtreservation.model.*
import it.polito.mad.g26.playingcourtreservation.model.custom.SportCenterServicesWithDetails

@Dao
interface SportCenterServicesDao {

    @Transaction
    @Query("SELECT * FROM sport_center_services WHERE id_sport_center = :idSportCenter")
    fun getAllServicesWithFee(idSportCenter: Int): LiveData<List<SportCenterServices>>

    @Query("SELECT * FROM sport_center_services WHERE id_sport_center = :idSportCenter")
    fun getAllServicesWithFeeDetailed(idSportCenter: Int): LiveData<List<SportCenterServicesWithDetails>>

}