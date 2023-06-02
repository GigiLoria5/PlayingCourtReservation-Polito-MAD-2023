package it.polito.mad.g26.playingcourtreservation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private var sport: String = ""
    private val myInvitees: MutableSet<String> = mutableSetOf()

    val initialMinAge = 22f
    val initialMaxAge = 70f
    val initialMinSkill = 2f
    val initialMaxSkill = 4f

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
        sport: String
    ) {
        this.city = city
        this.reservationId = reservationId
        this.date = date
        this.time = time
        this.sport = sport
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
    }

    /*USERNAME FILTER MANAGEMENT*/
    private val _filteredUsername = MutableLiveData("")
    fun changeFilteredUsername(username: String) {
        _filteredUsername.value = username.trim()
    }

    /*SELECTED MIN AGE MANAGEMENT*/
    private val _selectedMinAge = MutableLiveData(initialMinAge)
    fun changeSelectedMinAge(age: Float) {
        _selectedMinAge.value = age
    }

    /*SELECTED MAX AGE MANAGEMENT*/
    private val _selectedMaxAge = MutableLiveData(initialMaxAge)
    fun changeSelectedMaxAge(age: Float) {
        _selectedMaxAge.value = age
    }

    /*SELECTED MIN SKILL MANAGEMENT*/
    private val _selectedMinSkill = MutableLiveData(initialMinSkill)
    fun changeSelectedMinSkill(skillValue: Float) {
        _selectedMinSkill.value = skillValue
    }

    /*SELECTED MAX SKILL MANAGEMENT*/
    private val _selectedMaxSkill = MutableLiveData(initialMaxSkill)
    fun changeSelectedMaxSkill(skillValue: Float) {
        _selectedMaxSkill.value = skillValue
    }

    // Load users data
    private val _loadingState = MutableLiveData<UiState<Unit>>()
    val loadingState: LiveData<UiState<Unit>>
        get() = _loadingState

    private var users: List<User> = listOf()

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

        //can't apply this filter to db query because firebase supports Lists of max 10 elements
        users = allUsers.filter { !notInvitablePeople.contains(it.id) }
        _loadingState.value = UiState.Success(Unit)
    }

    fun getFilteredUsers(): List<User> {
        return users
            .filter { user ->
                val ageString = user.getAgeOrDefault()
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
    }

    fun isUserIdInvited(userId: String): Boolean = myInvitees.contains(userId)

}
