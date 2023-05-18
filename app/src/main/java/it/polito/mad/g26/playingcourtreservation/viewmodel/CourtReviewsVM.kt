package it.polito.mad.g26.playingcourtreservation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.model.Court
import it.polito.mad.g26.playingcourtreservation.model.Review
import it.polito.mad.g26.playingcourtreservation.repository.CourtRepository
import it.polito.mad.g26.playingcourtreservation.repository.ReviewRepository
import it.polito.mad.g26.playingcourtreservation.repository.SportCenterRepository

class CourtReviewsVM(application: Application) : AndroidViewModel(application) {

    private val courtRepo = CourtRepository(application)
    private val sportCenterRepo = SportCenterRepository(application)
    private val reviewRepo = ReviewRepository(application)
    fun getCourt(courtId: Int): LiveData<Court> = courtRepo.getCourt(courtId)
    fun getSportCenterName(sportCenterId: Int): LiveData<String> = sportCenterRepo.getSportCenterName(sportCenterId)
    fun courtReviews(courtId: Int): LiveData<List<Review>> = reviewRepo.courtReviews(courtId)
    fun courtReviewsCount(courtId: Int): LiveData<Int> = reviewRepo.courtReviewsCount(courtId)
    fun courtReviewsMean(courtId: Int): LiveData<Float> = reviewRepo.courtReviewsMean(courtId)
}
