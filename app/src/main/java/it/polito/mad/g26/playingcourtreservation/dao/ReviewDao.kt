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

    @Query("SELECT review.* from review INNER JOIN reservation ON " +
            "review.id_reservation = reservation.id " +
            "WHERE id_court =:courtId ORDER BY DATE(review.date) DESC")
    fun findAllCourtReviews(courtId: Int): LiveData<List<Review>>
    @Query("SELECT count() from review INNER JOIN reservation ON " +
            "review.id_reservation = reservation.id " +
            "WHERE id_court =:courtId")
    fun courtReviewsCount(courtId: Int): LiveData<Int>

    @Query("SELECT avg(rating) from review INNER JOIN reservation ON " +
            "review.id_reservation = reservation.id " +
            "WHERE id_court =:courtId")
    fun courtReviewsMean(courtId: Int): LiveData<Float>
}