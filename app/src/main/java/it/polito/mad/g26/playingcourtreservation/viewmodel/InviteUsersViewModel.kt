package it.polito.mad.g26.playingcourtreservation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.g26.playingcourtreservation.model.Notification
import it.polito.mad.g26.playingcourtreservation.model.User
import it.polito.mad.g26.playingcourtreservation.repository.NotificationRepository
import it.polito.mad.g26.playingcourtreservation.repository.ReservationRepository
import it.polito.mad.g26.playingcourtreservation.repository.UserRepository
import it.polito.mad.g26.playingcourtreservation.util.UiState
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InviteUsersViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository,
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private var city: String = ""
    private var reservationId: String = ""
    private var date: String = ""
    private var time: String = ""
    private var sport: String = ""
    private val myInvitees: MutableSet<String> = mutableSetOf()

    val minAge = 18f
    val maxAge = 90f
    val minSkill = 0f
    val maxSkill = 5f


    /* INITIALIZATION */
    fun initialize(
        city: String,
        reservationId: String,
        date: String,
        time: String,
        sport: String,
        allPositions: Set<String>
    ) {
        this.city = city
        this.reservationId = reservationId
        this.date = date
        this.time = time
        this.sport = sport
        this._selectedPositions.value = allPositions.toMutableSet()

        _filteredUsername.observeForever {
            fetchUsersData()
        }
        _selectedMinAge.observeForever {
            fetchUsersData()
        }
        _selectedMaxAge.observeForever {
            fetchUsersData()
        }
        _selectedMinSkill.observeForever {
            fetchUsersData()
        }
        _selectedMaxSkill.observeForever {
            fetchUsersData()
        }
        _selectedPositions.observeForever {
            fetchUsersData()
        }
    }

    /*USERNAME FILTER MANAGEMENT*/
    private val _filteredUsername = MutableLiveData("")
    fun changeFilteredUsername(username: String) {
        _filteredUsername.value = username.trim()
    }

    /*SELECTED MIN AGE MANAGEMENT*/
    private val _selectedMinAge = MutableLiveData(minAge)
    fun changeSelectedMinAge(age: Float) {
        _selectedMinAge.value = age
    }

    /*SELECTED MAX AGE MANAGEMENT*/
    private val _selectedMaxAge = MutableLiveData(maxAge)
    fun changeSelectedMaxAge(age: Float) {
        _selectedMaxAge.value = age
    }

    /*SELECTED MIN SKILL MANAGEMENT*/
    private val _selectedMinSkill = MutableLiveData(minSkill)
    fun changeSelectedMinSkill(skillValue: Float) {
        _selectedMinSkill.value = skillValue
    }

    /*SELECTED MAX SKILL MANAGEMENT*/
    private val _selectedMaxSkill = MutableLiveData(maxSkill)
    fun changeSelectedMaxSkill(skillValue: Float) {
        _selectedMaxSkill.value = skillValue
    }

    /*POSITIONS MANAGEMENT*/
    private var _selectedPositions = MutableLiveData<MutableSet<String>>().also {
        it.value = mutableSetOf()
    }

    fun addPositionToFilters(position: String) {
        val s = _selectedPositions.value
        s?.add(position)
        _selectedPositions.value = s
    }

    fun removePositionFromFilters(position: String) {
        val s = _selectedPositions.value
        s?.remove(position)
        _selectedPositions.value = s
    }

    fun isPositionInList(position: String): Boolean {
        return _selectedPositions.value?.contains(position) ?: false
    }

    fun numberOfSelectedPositions(): Int = _selectedPositions.value?.size ?: 0


    /*USER DATA MANAGEMENT*/
    private val _loadingState = MutableLiveData<UiState<Unit>>()
    val loadingState: LiveData<UiState<Unit>>
        get() = _loadingState

    private var _users :List<User> = listOf()
    val users: List<User>
        get() = _users

    private var _userPicturesMap: HashMap<String, ByteArray?> = hashMapOf() // K = userId
    val userPicturesMap: HashMap<String, ByteArray?>
        get() = _userPicturesMap
    fun fetchUsersData() = viewModelScope.launch {
        _loadingState.value = UiState.Loading
        // Get all reservations for the specified date/time
        val reservationsState =
            reservationRepository.getReservationsAt(date, time)
        if (reservationsState is UiState.Failure) {
            _loadingState.value = reservationsState
            return@launch
        }

        val reservations = (reservationsState as UiState.Success).result

        val myReservation = reservations.find { it.id == reservationId }!!
        myReservation.invitees.forEach { myInvitees.add(it) }
        val myRequests = myReservation.requests

        val notInvitablePeople = mutableSetOf<String>()
        myRequests.forEach { notInvitablePeople.add(it) }
        reservations.forEach { reservation ->
            notInvitablePeople.add(reservation.userId)
            reservation.participants.forEach { participant -> notInvitablePeople.add(participant) }
        }
        // Get all users
        val allUsersState = userRepository.getAllUsers()
        if (allUsersState is UiState.Failure) {
            _loadingState.value = allUsersState
            return@launch
        }
        val allUsers = (allUsersState as UiState.Success).result
        val invitableUsers = allUsers.filter { !notInvitablePeople.contains(it.id) }

        _users=getFilteredUsers(invitableUsers)

        val deferredUserPictures = _users.map { user ->
            async {
               val state= userRepository.downloadUserImage(user.id)
                val data= object{
                    val userId=user.id
                    val state=state
                }
                data
            }
        }
        val userPicturesResult = deferredUserPictures.awaitAll()
        for (data in userPicturesResult) {
            when (val state=data.state) {
                is UiState.Success -> {
                    _userPicturesMap[data.userId] = state.result
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

    private fun getFilteredUsers(invitableUsers: List<User>): List<User> {
        val filteredUsers = invitableUsers
            .filter { user ->
                _selectedPositions.value?.contains(user.position) ?: false
            }
            .filter { user ->
                val ageString = user.ageOrDefault()
                ageString != User.DEFAULT_BIRTHDATE
                        && ageString.toInt() >= _selectedMinAge.value!!.toInt()
                        && ageString.toInt() <= _selectedMaxAge.value!!.toInt()
            }
            .filter { user ->
                val skillValue = user.skills.find { it.sportName == sport }?.rating ?: 0f
                skillValue >= _selectedMinSkill.value!! && skillValue <= _selectedMaxSkill.value!!
            }
            .filter { user ->
                _filteredUsername.value!!.isEmpty() || user.username.contains(
                    _filteredUsername.value!!,
                    ignoreCase = true
                )
            }
        val cityUsers = filteredUsers.filter { user -> user.location == city }
            .sortedBy { user -> user.username }
        val notCityUsers = filteredUsers.filter { user -> user.location != city }
            .sortedBy { user -> user.username }
        return cityUsers + notCityUsers
    }

    fun isUserIdInvited(userId: String): Boolean = myInvitees.contains(userId)

    /*INVITATIONS MANAGEMENT*/

    // Handle Invitation
    private val _invitationState = MutableLiveData<UiState<Unit>>()
    val invitationState: LiveData<UiState<Unit>>
        get() = _invitationState
    fun inviteAndNotifyUser(userId: String) = viewModelScope.launch {
        _invitationState.value = UiState.Loading
        _loadingState.value = UiState.Loading
        // Invite user
        val state = reservationRepository.inviteUser(reservationId, userId)
        if (state is UiState.Failure) {
            _invitationState.value = state
            return@launch
        }

        // Send notification
        val notification = Notification.matchInvitation(
            userId,
            reservationId
        )
        notificationRepository.saveNotification(notification)
        /* since the invitation has already been sent at this point,
        it is not worth checking whether the notification has been sent correctly or not
        */

        _invitationState.value = UiState.Success(Unit)
        fetchUsersData()
    }
}
