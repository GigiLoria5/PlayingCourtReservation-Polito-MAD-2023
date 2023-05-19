package it.polito.mad.g26.playingcourtreservation.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import it.polito.mad.g26.playingcourtreservation.model.*
import it.polito.mad.g26.playingcourtreservation.model.custom.SportCenterReviewsSummary

@Dao
interface ReviewDao {

    @Query("SELECT * from review")
    fun findAll(): LiveData<List<Review>>


    @Query(
        "Select COUNT(*) as count, AVG(rating) as avg, court.id_sport_center as sportCenterId " +
                "from review, reservation,court " +
                "where review.id_reservation=reservation.id and reservation.id_court=court.id and court.id_sport_center=:sportCenterId"
    )
    fun findSummaryBySportCenterId(sportCenterId: Int): LiveData<SportCenterReviewsSummary>

    @Query(
        "Select COUNT(*) as count, AVG(rating) as avg,court.id_sport_center as sportCenterId " +
                "from review, reservation,court " +
                "where review.id_reservation=reservation.id and reservation.id_court=court.id and court.id_sport_center IN (:sportCentersIds) " +
                "GROUP BY court.id_sport_center "
    )
    fun findSummariesBySportCentersIds(sportCentersIds: Set<Int>): LiveData<List<SportCenterReviewsSummary>>
}