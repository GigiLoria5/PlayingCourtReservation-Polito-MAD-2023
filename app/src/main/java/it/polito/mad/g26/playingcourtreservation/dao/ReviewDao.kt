package it.polito.mad.g26.playingcourtreservation.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import it.polito.mad.g26.playingcourtreservation.model.*

@Dao
interface ReviewDao {

    @Query("SELECT * from review")
    fun findAll(): LiveData<List<Review>>

    @Insert
    fun addReview(reservation: Review)

}