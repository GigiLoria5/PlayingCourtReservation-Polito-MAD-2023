package it.polito.mad.g26.playingcourtreservation.repository

import android.app.Application
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.database.CourtReservationDatabase
import it.polito.mad.g26.playingcourtreservation.model.*
import it.polito.mad.g26.playingcourtreservation.model.custom.SportCenterReviewsSummary

class ReviewRepository(application: Application) {
    private val reviewDao = CourtReservationDatabase.getDatabase(application).reviewDao()

    fun reviews(): LiveData<List<Review>> = reviewDao.findAll()
    fun addReview(idReservation: Int, idUser: Int, rating: Float, textReview: String, date:String){
        val review = Review().also {
            it.idReservation = idReservation
            it.idUser = idUser
            it.rating = rating
            it.text = textReview
            it.date = date
        }
        return reviewDao.addReview(review)
    }

    fun courtReviews(courtId: Int): LiveData<List<Review>> = reviewDao.findAllCourtReviews(courtId)

    fun courtReviewsCount(courtId: Int): LiveData<Int> = reviewDao.courtReviewsCount(courtId)
    fun courtReviewsMean(courtId: Int): LiveData<Float> = reviewDao.courtReviewsMean(courtId)

    fun reviewsSummaryBySportCenterId(sportCenterId: Int): LiveData<SportCenterReviewsSummary> =
        reviewDao.findSummaryBySportCenterId(sportCenterId)

    fun reviewsSummariesBySportCentersIds(sportCentersIds: Set<Int>): LiveData<List<SportCenterReviewsSummary>> =
        reviewDao.findSummariesBySportCentersIds(sportCentersIds)
}