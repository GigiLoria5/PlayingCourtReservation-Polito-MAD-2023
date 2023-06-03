package it.polito.mad.g26.playingcourtreservation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.g26.playingcourtreservation.model.Notification
import it.polito.mad.g26.playingcourtreservation.model.Reservation
import it.polito.mad.g26.playingcourtreservation.model.Review
import it.polito.mad.g26.playingcourtreservation.model.SportCenter
import it.polito.mad.g26.playingcourtreservation.model.User
import it.polito.mad.g26.playingcourtreservation.repository.NotificationRepository
import it.polito.mad.g26.playingcourtreservation.repository.ReservationRepository
import it.polito.mad.g26.playingcourtreservation.repository.SportCenterRepository
import it.polito.mad.g26.playingcourtreservation.repository.UserRepository
import it.polito.mad.g26.playingcourtreservation.util.UiState
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ReservationDetailsViewModel @Inject constructor(
    private val sportCenterRepository: SportCenterRepository,
    private val reservationRepository: ReservationRepository,
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository
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

    private var _user: User = User()
    val user: User
        get() = _user

    private var _participants: MutableList<User> = mutableListOf()
    val participants: List<User>
        get() = _participants

    private var _requesters: MutableList<User> = mutableListOf()
    val requesters: List<User>
        get() = _requesters

    fun loadReservationAndSportCenterInformation() = viewModelScope.launch {
        _loadingState.value = UiState.Loading
        // Get reservation details
        val reservationState = reservationRepository.getReservationById(_reservationId)
        if (reservationState is UiState.Failure) {
            _loadingState.value = reservationState
            return@launch
        }
        _reservation = (reservationState as UiState.Success).result
        // Get review (if any)
        _review = _reservation.reviews.firstOrNull { it.userId == userId }
        // Get sport center information where the reservation has been made
        val sportCenterState = sportCenterRepository.getSportCenterById(_reservation.sportCenterId)
        if (sportCenterState is UiState.Failure) {
            _loadingState.value = sportCenterState
            return@launch
        }
        _sportCenter = (sportCenterState as UiState.Success).result
        val userState = userRepository.getCurrentUserInformation()
        if (userState is UiState.Failure) {
            _loadingState.value = userState
            return@launch
        }
        _user = (userState as UiState.Success).result
        //Get participants List<User> from List<String>
        val deferredParticipants = _reservation.participants.map { participantID ->
            async {
                userRepository.getUserInformationById(participantID)
            }
        }
        val participantsResult = deferredParticipants.awaitAll()
        for ((index, state) in participantsResult.withIndex()) {
            when (state) {
                is UiState.Success -> {
                    _participants.add(state.result)
                }

                is UiState.Failure -> {
                    _loadingState.value = state
                }

                else -> {
                    _loadingState.value = UiState.Failure(null)
                }
            }
        }
        //Get requesters List<User> from List<String>
        val deferredRequesters = _reservation.requests.map { requesterID ->
            async {
                userRepository.getUserInformationById(requesterID)
            }
        }
        val requestersResult = deferredRequesters.awaitAll()
        for ((index, state) in requestersResult.withIndex()) {
            when (state) {
                is UiState.Success -> {
                    _requesters.add(state.result)
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

    fun deleteUserReview() = viewModelScope.launch {
        _loadingState.value = UiState.Loading
        val state = reservationRepository.deleteUserReview(_reservationId, userId)
        if (state is UiState.Failure) {
            _loadingState.value = state
            return@launch
        }
        delay(500)
        loadReservationAndSportCenterInformation() // To update the UI
    }

    // Handle Reservation Delete
    private val _deleteState = MutableLiveData<UiState<Unit>>()
    val deleteState: LiveData<UiState<Unit>>
        get() = _deleteState

    fun deleteReservation() = viewModelScope.launch {
        _deleteState.value = UiState.Loading
        // Delete Reservation
        val state = reservationRepository.deleteReservation(_reservationId)
        if (state is UiState.Failure) {
            _deleteState.value = state
            return@launch
        }
        // Send notification to participants
        val participants = reservation.participants
        val deferredNotifications = participants.map { participantId ->
            async {
                val notification = Notification.matchCancelled(
                    participantId,
                    reservationId,
                    reservation.date,
                    reservation.time
                )
                notificationRepository.saveNotification(notification)
            }
        }
        deferredNotifications.awaitAll()
        /* since the reservation has already been cancelled at this point,
           it is not worth checking whether the notifications have been sent correctly or not
         */
        _deleteState.value = state
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
