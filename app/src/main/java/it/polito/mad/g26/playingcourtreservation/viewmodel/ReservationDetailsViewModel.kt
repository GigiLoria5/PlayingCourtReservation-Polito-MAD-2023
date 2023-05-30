package it.polito.mad.g26.playingcourtreservation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.g26.playingcourtreservation.newModel.Reservation
import it.polito.mad.g26.playingcourtreservation.newModel.Review
import it.polito.mad.g26.playingcourtreservation.newModel.SportCenter
import it.polito.mad.g26.playingcourtreservation.newRepository.ReservationRepository
import it.polito.mad.g26.playingcourtreservation.newRepository.SportCenterRepository
import it.polito.mad.g26.playingcourtreservation.newRepository.UserRepository
import it.polito.mad.g26.playingcourtreservation.util.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ReservationDetailsViewModel @Inject constructor(
    private val sportCenterRepository: SportCenterRepository,
    private val reservationRepository: ReservationRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    /* INITIALIZATION */
    private var reservationId: String = ""

    fun initialize(reservationId: String) {
        this.reservationId = reservationId
    }

    // Load reservation data
    private val _loadingState = MutableLiveData<UiState<Unit>>()
    val loadingState: LiveData<UiState<Unit>>
        get() = _loadingState

    private var _reservation: Reservation = Reservation()
    val reservation: Reservation
        get() = _reservation

    private var _review: Review? = null
    val review: Review?
        get() = _review

    private var _sportCenter: SportCenter = SportCenter()
    val sportCenter: SportCenter
        get() = _sportCenter

    fun getReservationAndAllSportCenterServices() = viewModelScope.launch {
        _loadingState.value = UiState.Loading
        // Get reservation details
        val reservationState = reservationRepository.getReservationById(reservationId)
        if (reservationState is UiState.Failure) {
            _loadingState.value = reservationState
            return@launch
        }
        _reservation = (reservationState as UiState.Success).result
        // Get all services offered by the sport center where the reservation has been made
        val servicesState = sportCenterRepository.getSportCenterById(_reservation.sportCenterId)
        if (servicesState is UiState.Failure) {
            _loadingState.value = servicesState
            return@launch
        }
        _sportCenter = (servicesState as UiState.Success).result
        _loadingState.value = UiState.Success(Unit)
    }

    // Other functions
    fun deleteUserReview() = viewModelScope.launch {
        _loadingState.value = UiState.Loading
        reservationRepository.deleteUserReview(reservationId, userRepository.currentUser!!.uid)
        delay(500)
        _loadingState.value = UiState.Success(Unit)
    }

    fun deleteReservation() = viewModelScope.launch {
        _loadingState.value = UiState.Loading
        reservationRepository.deleteReservation(reservationId)
        delay(500)
    }

    // Utils
    fun nowIsBeforeReservationDateTime(): Boolean {
        val reservationDate = LocalDateTime.parse(
            "${reservation.date} ${reservation.time}",
            DateTimeFormatter.ofPattern("${Reservation.getDatePattern()} ${Reservation.getTimePattern()}")
        )
        val now = LocalDateTime.now()
        return now.isBefore(reservationDate)
    }
}