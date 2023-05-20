package it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import it.polito.mad.g26.playingcourtreservation.repository.ReservationRepository
import it.polito.mad.g26.playingcourtreservation.repository.ReviewRepository
import it.polito.mad.g26.playingcourtreservation.repository.SportCenterRepository

class SearchCourtsVM(application: Application) : AndroidViewModel(application) {

    private val sportCenterRepository = SportCenterRepository(application)
    private val reservationRepository = ReservationRepository(application)
    private val reviewRepository = ReviewRepository(application)

    /*SPORT CENTER MANAGEMENT*/
    private val sportCenterIdLiveData = MutableLiveData(0)

    fun setSportCenterId(id: Int) {
        sportCenterIdLiveData.value = id
    }

    val sportCenter =
        sportCenterIdLiveData.switchMap { sportCenterId ->
            sportCenterRepository.sportCenterById(sportCenterId)
        }

    val reviews = sportCenter.switchMap { sportCenter ->
        reviewRepository.reviewsSummaryBySportCenterId(sportCenter.sportCenter.id)
    }

}