package it.polito.mad.g26.playingcourtreservation.repository

import android.app.Application
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.database.CourtReservationDatabase
import it.polito.mad.g26.playingcourtreservation.model.*
import it.polito.mad.g26.playingcourtreservation.model.custom.CourtReviewsSummary
import it.polito.mad.g26.playingcourtreservation.model.custom.SportCenterReviewsSummary

class ReviewRepository(application: Application) {
    private val reviewDao = CourtReservationDatabase.getDatabase(application).reviewDao()

    fun reviews(): LiveData<List<Review>> = reviewDao.findAll()

    fun reviewsSummaryBySportCenterId(sportCenterId: Int): LiveData<List<CourtReviewsSummary>> =
        reviewDao.findSummaryBySportCenterId(sportCenterId)

    fun reviewsSummariesBySportCentersIds(sportCentersIds: Set<Int>): LiveData<List<SportCenterReviewsSummary>> =
        reviewDao.findSummariesBySportCentersIds(sportCentersIds)
}