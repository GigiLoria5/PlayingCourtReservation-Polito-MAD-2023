package it.polito.mad.g26.playingcourtreservation.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import it.polito.mad.g26.playingcourtreservation.model.*
import it.polito.mad.g26.playingcourtreservation.model.custom.CourtReviewsSummary
import it.polito.mad.g26.playingcourtreservation.model.custom.SportCenterReviewsSummary

@Dao
interface ReviewDao {

    @Query("SELECT * from review")
    fun findAll(): LiveData<List<Review>>


    @Query(
        "Select COUNT(*) as count, AVG(rating) as avg, court.id as courtId " +
                "from review, reservation,court " +
                "where review.id_reservation=reservation.id and reservation.id_court=court.id and court.id_sport_center=:sportCenterId "+
                "GROUP BY court.id "
    )
    fun findSummaryBySportCenterId(sportCenterId: Int): LiveData<List<CourtReviewsSummary>>

    @Query(
        "Select COUNT(*) as count, AVG(rating) as avg,court.id_sport_center as sportCenterId " +
                "from review, reservation,court " +
                "where review.id_reservation=reservation.id and reservation.id_court=court.id and court.id_sport_center IN (:sportCentersIds) " +
                "GROUP BY court.id_sport_center "
    )
    fun findSummariesBySportCentersIds(sportCentersIds: Set<Int>): LiveData<List<SportCenterReviewsSummary>>
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

    @Query("SELECT * from review WHERE id_reservation =:reservationId AND id_user=:userId")
    fun findReservationReview(reservationId: Int, userId: Int): LiveData<Review?>
    @Query("DELETE FROM review WHERE id_reservation = :reservationId AND id_user=:userId")
    fun deleteReviewById(reservationId: Int, userId: Int)

    @Query("UPDATE review SET rating=:rating, text=:textReview, date=:date WHERE id_reservation=:idReservation AND id_user=:idUser")
    fun updateReview(idReservation: Int, idUser: Int, rating: Float, textReview: String, date:String)
}