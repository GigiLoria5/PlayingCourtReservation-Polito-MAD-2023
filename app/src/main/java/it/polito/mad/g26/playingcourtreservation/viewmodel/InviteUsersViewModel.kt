package it.polito.mad.g26.playingcourtreservation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.g26.playingcourtreservation.model.Reservation
import it.polito.mad.g26.playingcourtreservation.model.User
import it.polito.mad.g26.playingcourtreservation.repository.ReservationRepository
import it.polito.mad.g26.playingcourtreservation.repository.UserRepository
import it.polito.mad.g26.playingcourtreservation.util.UiState
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InviteUsersViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private var city: String = ""
    private var reservationId: String = ""
    private var date: String = ""
    private var time: String = ""

    /* INITIALIZATION */
    fun initialize(
        city: String,
        reservationId: String,
        date: String,
        time: String
    ) {
        this.city = city
        this.reservationId = reservationId
        this.date = date
        this.time = time
    }

    // Load users data
    private val _loadingState = MutableLiveData<UiState<Unit>>()
    val loadingState: LiveData<UiState<Unit>>
        get() = _loadingState

    private var _users: List<User> = listOf()
    val users: List<User>
        get() = _users

    private var _reservations: List<Reservation> = listOf()

    val reservations: List<Reservation>
        get() = _reservations

    fun fetchUsersData() = viewModelScope.launch {
        _loadingState.value = UiState.Loading
        // Get all reservations for the specified date/time
        val reservationsState =
        reservationRepository.getReservationsAt(date,time)
        if (reservationsState is UiState.Failure) {
            _loadingState.value = reservationsState
            return@launch
        }
        _reservations = (reservationsState as UiState.Success).result
        val myReservation=_reservations.find { it.id==reservationId }!!
        val myInvitees=myReservation.invitees
        val myRequests=myReservation.requests
        val notInvitablePeople= mutableSetOf<String>()
        myRequests.forEach { notInvitablePeople.add(it) }
        _reservations.forEach {reservation->
            notInvitablePeople.add(reservation.userId)
            reservation.participants.forEach {participant->notInvitablePeople.add(participant)  }
        }
        _reservations=reservations
        notInvitablePeople.forEach { println(it) }

        //ORA DOVRAI UTILIZZARE NOT INVITABLE PEOPLE PER FILTRARE GLI UTENTI
        _loadingState.value = UiState.Success(Unit)
    }

    fun getTotAvailableUsers(): Int = users.size

}
