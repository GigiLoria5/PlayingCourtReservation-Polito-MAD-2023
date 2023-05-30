package it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.g26.playingcourtreservation.newModel.Court
import it.polito.mad.g26.playingcourtreservation.newModel.Reservation
import it.polito.mad.g26.playingcourtreservation.newModel.Review
import it.polito.mad.g26.playingcourtreservation.newRepository.ReservationRepository
import it.polito.mad.g26.playingcourtreservation.newRepository.SportCenterRepository
import it.polito.mad.g26.playingcourtreservation.util.SearchSportCentersUtil
import it.polito.mad.g26.playingcourtreservation.util.UiState
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchCourtsViewModel @Inject constructor(
    private val sportCenterRepository: SportCenterRepository,
    private val reservationRepository: ReservationRepository
) : ViewModel() {

    private var sportCenterId: String = ""
    private var sportName: String = ""
    private var dateTime: Long = 0

    /* INITIALIZATION */
    fun initialize(sportCenterId: String, sportName: String, dateTime: Long) {
        this.sportCenterId = sportCenterId
        this.sportName = sportName
        this.dateTime = dateTime
    }

    // Load courts data
    private val _loadingState = MutableLiveData<UiState<Unit>>()
    val loadingState: LiveData<UiState<Unit>>
        get() = _loadingState

    private var _courts: List<Court> = listOf()
    val courts: List<Court>
        get() = _courts

    private var _reviews: HashMap<String, List<Review>> = hashMapOf()
    val reviews: HashMap<String, List<Review>>
        get() = _reviews

    private var reservations: HashMap<String, Reservation?> = hashMapOf()

    fun fetchCourtsData() = viewModelScope.launch {
        _loadingState.value = UiState.Loading
        // Get all sport center's courts with the chosen sport
        val courtsState =
            if (sportName == "All sports")
                sportCenterRepository.getAllSportCenterCourts(sportCenterId)
            else
                sportCenterRepository.getAllSportCenterCourtsBySport(sportCenterId, sportName)
        if (courtsState is UiState.Failure) {
            _loadingState.value = courtsState
            return@launch
        }
        _courts = (courtsState as UiState.Success).result
        // For each court get all reviews and (possible) reservation for the selected day and time
        val deferredReviews = _courts.map { court ->
            async {
                reservationRepository.getCourtReviews(court.id)
            }
        }
        val deferredReservation = _courts.map { court ->
            async {
                reservationRepository.getCourtReservationAt(
                    court.id,
                    getDateTimeFormatted(dateFormat),
                    getDateTimeFormatted(timeFormat)
                )
            }
        }
        val reviewsResults = deferredReviews.awaitAll()
        val reservationResults = deferredReservation.awaitAll()
        // Populate Reviews HashMap
        for ((index, state) in reviewsResults.withIndex()) {
            when (state) {
                is UiState.Success -> {
                    _reviews[_courts[index].id] = state.result
                }

                is UiState.Failure -> {
                    _loadingState.value = state
                }

                else -> {
                    _loadingState.value = UiState.Failure(null)
                }
            }
        }
        // Populate Reservation HashMap
        for ((index, state) in reservationResults.withIndex()) {
            when (state) {
                is UiState.Success -> {
                    reservations[_courts[index].id] = state.result
                }

                is UiState.Failure -> {
                    _loadingState.value = state
                }

                else -> {
                    _loadingState.value = UiState.Failure(null)
                }
            }
        }
        _loadingState.value = UiState.Success(Unit)
    }

    /*DATE TIME MANAGEMENT*/
    private val dateFormat = Reservation.getDatePattern()
    private val timeFormat = Reservation.getTimePattern()

    private fun getDateTimeFormatted(format: String): String {
        return SearchSportCentersUtil.getDateTimeFormatted(
            dateTime,
            format
        )
    }

    /*RESERVATIONS MANAGEMENT*/
    fun isCourtAvailable(courtId: String): Boolean {
        return reservations[courtId] == null
    }

    fun getTotAvailableCourts(): Int = courts.size - reservations.count { it.value != null }

}
