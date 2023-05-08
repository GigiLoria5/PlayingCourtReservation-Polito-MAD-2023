package it.polito.mad.g26.playingcourtreservation.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import it.polito.mad.g26.playingcourtreservation.model.*

@Dao
interface ServiceDao {
    @Query("SELECT * FROM service")
    fun findAll(): LiveData<List<Service>>

    @Transaction
    @Query("SELECT * FROM service WHERE id = :id")
    fun getServiceById(id:Int) : LiveData<Service>

}