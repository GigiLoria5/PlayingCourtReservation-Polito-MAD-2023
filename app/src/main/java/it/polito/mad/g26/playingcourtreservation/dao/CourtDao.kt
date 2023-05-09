package it.polito.mad.g26.playingcourtreservation.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import it.polito.mad.g26.playingcourtreservation.model.*

@Dao
fun interface CourtDao {

    @Query("SELECT * from court")
    fun findAllCourt(): LiveData<List<Court>>

}