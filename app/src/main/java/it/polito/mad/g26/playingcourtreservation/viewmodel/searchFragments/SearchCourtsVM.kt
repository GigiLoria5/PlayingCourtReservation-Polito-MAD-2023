package it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import it.polito.mad.g26.playingcourtreservation.model.CourtWithDetails
import it.polito.mad.g26.playingcourtreservation.model.Reservation
import it.polito.mad.g26.playingcourtreservation.repository.CourtRepository
import it.polito.mad.g26.playingcourtreservation.repository.ReservationRepository
import it.polito.mad.g26.playingcourtreservation.repository.ReviewRepository
import it.polito.mad.g26.playingcourtreservation.util.SearchSportCentersUtil

class SearchCourtsVM(application: Application) : AndroidViewModel(application) {

    private val courtRepository = CourtRepository(application)
    private val reservationRepository = ReservationRepository(application)
    private val reviewRepository = ReviewRepository(application)

    /*DATE TIME MANAGEMENT*/
    private val dateFormat = Reservation.getReservationDatePattern()
    private val timeFormat = Reservation.getReservationTimePattern()
    private var dateTime: Long = 0
    fun setDateTime(selectedDateTime: Long) {
        dateTime = selectedDateTime
    }

    private fun getDateTimeFormatted(format: String): String {
        return SearchSportCentersUtil.getDateTimeFormatted(
            dateTime,
            format
        )
    }

    /*SPORT MANAGEMENT*/
    private var sportId: Int = 0
    fun setSportId(selectedSportId: Int) {
        sportId = selectedSportId
    }

    /*SPORT CENTER MANAGEMENT*/
    private val sportCenterIdLiveData = MutableLiveData(0)

    fun setSportCenterId(id: Int) {
        sportCenterIdLiveData.value = id
    }

    private val courts =
        sportCenterIdLiveData.switchMap { sportCenterId ->
            courtRepository.getCourtsBySportCenterId(sportCenterId)
        }

    fun getCourtsBySelectedSport(): List<CourtWithDetails> {
        return courts.value?.filter { court ->
            sportId == 0 || court.sport.id == sportId

        } ?: listOf()
    }

    /*RESERVATIONS MANAGEMENT*/
    private val reservations: LiveData<List<Reservation>> = courts.switchMap {
        val courtsIdList = it.map { court -> court.court.id }
        val date = getDateTimeFormatted(dateFormat)
        val hour = getDateTimeFormatted(timeFormat)
        reservationRepository.filteredReservations(date, hour, courtsIdList)
    }

    fun getTotAvailableCourts(): Int = getCourtsBySelectedSport().size - reservations.value!!.size

    fun isCourtAvailable(courtId: Int): Boolean {
        return reservations.value?.find { it.idCourt == courtId }?.idUser == null
    }

    val reviews = reservations.switchMap {
        reviewRepository.reviewsSummaryBySportCenterId(sportCenterIdLiveData.value!!)
    }

}