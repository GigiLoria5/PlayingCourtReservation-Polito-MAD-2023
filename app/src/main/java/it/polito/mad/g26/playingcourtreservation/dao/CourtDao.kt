package it.polito.mad.g26.playingcourtreservation.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import it.polito.mad.g26.playingcourtreservation.model.*

@Dao
interface CourtDao {

    @Query("SELECT * from court")
    fun findAllCourt(): LiveData<List<Court>>

    @Query("SELECT * from court WHERE id=:courtId")
    fun findCourt(courtId: Int): LiveData<Court>

    @Query("SELECT * from court where id_sport_center=:sportCenterId")
    fun findBySportCenterId(sportCenterId: Int): LiveData<List<CourtWithDetails>>

}