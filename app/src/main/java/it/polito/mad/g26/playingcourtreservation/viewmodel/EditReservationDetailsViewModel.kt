package it.polito.mad.g26.playingcourtreservation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.g26.playingcourtreservation.newModel.Reservation
import it.polito.mad.g26.playingcourtreservation.newModel.SportCenter
import it.polito.mad.g26.playingcourtreservation.newRepository.ReservationRepository
import it.polito.mad.g26.playingcourtreservation.newRepository.SportCenterRepository
import it.polito.mad.g26.playingcourtreservation.newRepository.UserRepository
import it.polito.mad.g26.playingcourtreservation.util.ReservationWithDetailsUtil
import it.polito.mad.g26.playingcourtreservation.util.SearchSportCentersUtil
import it.polito.mad.g26.playingcourtreservation.util.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditReservationDetailsViewModel @Inject constructor(
    private val sportCenterRepository: SportCenterRepository,
    private val reservationRepository: ReservationRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    /* INITIALIZATION */
    private var _reservationId: String = ""
    val reservationId: String
        get() = _reservationId
    val userId: String
        get() = userRepository.currentUser!!.uid

    fun initialize(reservationId: String) {
        this._reservationId = reservationId
    }

    // DateTime management
    private val dateFormat = Reservation.getDatePattern()
    private val timeFormat = Reservation.getTimePattern()

    private val _selectedDateTimeMillis = MutableLiveData<Long>().also {
        it.value = ReservationWithDetailsUtil.getMockInitialDateTime().timeInMillis
    }
    val selectedDateTimeMillis: LiveData<Long> = _selectedDateTimeMillis

    private fun getDateTimeFormatted(format: String): String {
        return SearchSportCentersUtil.getDateTimeFormatted(
            selectedDateTimeMillis.value ?: 0,
            format
        )
    }

    fun changeSelectedDateTimeMillis(newTimeInMillis: Long) {
        _selectedDateTimeMillis.value = newTimeInMillis
    }

    // Load reservation data
    private val _loadingState = MutableLiveData<UiState<Unit>>()
    val loadingState: LiveData<UiState<Unit>>
        get() = _loadingState

    private var _reservation: Reservation = Reservation()
    val reservation: Reservation
        get() = _reservation

    private var _sportCenter: SportCenter = SportCenter()
    val sportCenter: SportCenter
        get() = _sportCenter

    fun loadReservationAndSportCenterInformation() = viewModelScope.launch {
        _loadingState.value = UiState.Loading
        // Get reservation details
        val reservationState = reservationRepository.getReservationById(_reservationId)
        if (reservationState is UiState.Failure) {
            _loadingState.value = reservationState
            return@launch
        }
        _reservation = (reservationState as UiState.Success).result
        // Get sport center information where the reservation has been made
        val sportCenterState = sportCenterRepository.getSportCenterById(_reservation.sportCenterId)
        if (sportCenterState is UiState.Failure) {
            _loadingState.value = sportCenterState
            return@launch
        }
        _sportCenter = (sportCenterState as UiState.Success).result
        _loadingState.value = UiState.Success(Unit)
    }

    // Update Reservation
    private val _updateState = MutableLiveData<UiState<Unit>>()
    val updateState: LiveData<UiState<Unit>>
        get() = _updateState

    fun updateReservation(updatedReservation: Reservation) = viewModelScope.launch {
        _updateState.value = UiState.Loading
        // Update date, time and id (eventually)
        val newDate = getDateTimeFormatted(dateFormat)
        val newTime = getDateTimeFormatted(timeFormat)
        updatedReservation.date = newDate
        updatedReservation.time = newTime
        updatedReservation.id = Reservation.generateId(
            updatedReservation.courtId,
            newDate,
            newTime
        )
        // Update Reservation
        val resultState = reservationRepository.updateReservation(reservationId, updatedReservation)
        delay(500)
        if (resultState is UiState.Success) {
            _reservation = updatedReservation
            _reservationId = updatedReservation.id
        }
        _updateState.value = resultState
    }

}
