package it.polito.mad.g26.playingcourtreservation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.g26.playingcourtreservation.model.Court
import it.polito.mad.g26.playingcourtreservation.model.Reservation
import it.polito.mad.g26.playingcourtreservation.model.SportCenter
import it.polito.mad.g26.playingcourtreservation.repository.ReservationRepository
import it.polito.mad.g26.playingcourtreservation.repository.SportCenterRepository
import it.polito.mad.g26.playingcourtreservation.repository.UserRepository
import it.polito.mad.g26.playingcourtreservation.util.ReservationDetailsUtils
import it.polito.mad.g26.playingcourtreservation.util.SearchSportCentersUtils
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
    val dateFormat = Reservation.getDatePattern()
    val timeFormat = Reservation.getTimePattern()

    private val _selectedDateTimeMillis = MutableLiveData<Long>().also {
        it.value = ReservationDetailsUtils.getMockInitialDateTime().timeInMillis
    }
    val selectedDateTimeMillis: LiveData<Long> = _selectedDateTimeMillis

    fun getDateTimeFormatted(format: String): String {
        return SearchSportCentersUtils.getDateTimeFormatted(
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

    private var _court: Court = Court()
    val court: Court
        get() = _court

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
        _court = _sportCenter.courts.first { it.id == reservation.courtId }
        _loadingState.value = UiState.Success(Unit)
    }

    // Update Reservation
    private val _updateState = MutableLiveData<UiState<Unit>>()
    val updateState: LiveData<UiState<Unit>>
        get() = _updateState

    fun updateReservation(updatedReservation: Reservation) = viewModelScope.launch {
        _updateState.value = UiState.Loading
        // Check if user is free
        val newDate = getDateTimeFormatted(dateFormat)
        val newTime = getDateTimeFormatted(timeFormat)
        val existingReservationState = reservationRepository.getUserReservationAt(
            userId, newDate, newTime
        )
        if (existingReservationState is UiState.Failure) {
            _updateState.value = existingReservationState
            return@launch
        }
        val existingReservation = (existingReservationState as UiState.Success).result
        println("Existing reservation: $existingReservation while actual reservation: $updatedReservation")
        if (existingReservation != null && existingReservation.id != updatedReservation.id) {
            _updateState.value =
                UiState.Failure("You already have reservation for this date and time")
            return@launch
        }
        // Update date, time, price and id (eventually)
        updatedReservation.date = newDate
        updatedReservation.time = newTime
        updatedReservation.id = Reservation.generateId(
            updatedReservation.courtId,
            newDate,
            newTime
        )
        updatedReservation.amount = getFinalPrice(updatedReservation)
        // Update Reservation
        val resultState = reservationRepository.updateReservation(reservationId, updatedReservation)
        delay(500)
        if (resultState is UiState.Success) {
            _reservation = updatedReservation
            _reservationId = updatedReservation.id
        }
        _updateState.value = resultState
    }

    private fun getFinalPrice(reservation: Reservation): Float {
        return court.hourCharge + sportCenter.services.filter { reservation.services.contains(it.name) }
            .map { it.fee }.sum()
    }

}
