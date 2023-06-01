package it.polito.mad.g26.playingcourtreservation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.g26.playingcourtreservation.model.Reservation
import it.polito.mad.g26.playingcourtreservation.model.SportCenter
import it.polito.mad.g26.playingcourtreservation.repository.ReservationRepository
import it.polito.mad.g26.playingcourtreservation.repository.SportCenterRepository
import it.polito.mad.g26.playingcourtreservation.repository.UserRepository
import it.polito.mad.g26.playingcourtreservation.util.UiState
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReservationsViewModel @Inject constructor(
    private val sportCenterRepository: SportCenterRepository,
    private val reservationRepository: ReservationRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    // Load courts data
    private val _loadingState = MutableLiveData<UiState<Unit>>()
    val loadingState: LiveData<UiState<Unit>>
        get() = _loadingState

    private var _reservations: List<Reservation> = listOf()
    val reservations: List<Reservation>
        get() = _reservations

    private var _sportCenters: HashMap<String, SportCenter> = hashMapOf()
    val sportCenters: HashMap<String, SportCenter>
        get() = _sportCenters

    fun loadUserReservation() = viewModelScope.launch {
        _loadingState.value = UiState.Loading
        val reservationsState = reservationRepository
            .getUserReservations(userRepository.currentUser!!.uid)
        if (reservationsState is UiState.Failure) {
            _loadingState.value = reservationsState
            return@launch
        }
        _reservations = (reservationsState as UiState.Success).result
        // For each reservation get sport center information
        val deferredSportCenters = _reservations.map { reservation ->
            async {
                sportCenterRepository.getSportCenterById(reservation.sportCenterId)
            }
        }
        val sportCentersResult = deferredSportCenters.awaitAll()
        for ((index, state) in sportCentersResult.withIndex()) {
            when (state) {
                is UiState.Success -> {
                    _sportCenters[_reservations[index].id] = state.result
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

}
