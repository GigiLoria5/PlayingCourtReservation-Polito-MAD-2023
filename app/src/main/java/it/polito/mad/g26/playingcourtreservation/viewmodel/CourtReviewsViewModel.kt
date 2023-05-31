package it.polito.mad.g26.playingcourtreservation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.g26.playingcourtreservation.newModel.Court
import it.polito.mad.g26.playingcourtreservation.newModel.Review
import it.polito.mad.g26.playingcourtreservation.newModel.SportCenter
import it.polito.mad.g26.playingcourtreservation.newModel.User
import it.polito.mad.g26.playingcourtreservation.newRepository.ReservationRepository
import it.polito.mad.g26.playingcourtreservation.newRepository.SportCenterRepository
import it.polito.mad.g26.playingcourtreservation.newRepository.UserRepository
import it.polito.mad.g26.playingcourtreservation.util.UiState
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourtReviewsViewModel @Inject constructor(
    private val sportCenterRepository: SportCenterRepository,
    private val reservationRepository: ReservationRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    // Load court reviews
    private val _loadingState = MutableLiveData<UiState<Unit>>()
    val loadingState: LiveData<UiState<Unit>>
        get() = _loadingState

    private var _sportCenter: SportCenter = SportCenter()
    val sportCenter: SportCenter
        get() = _sportCenter

    private var _court: Court = Court()
    val court: Court
        get() = _court

    private var _courtReviews: List<Review> = listOf()
    val courtReviews: List<Review>
        get() = _courtReviews

    private var _userInformationMap: HashMap<String, User> = hashMapOf() // K = userId
    val userInformationMap: HashMap<String, User>
        get() = _userInformationMap

    fun loadCourtReviews(sportCenterId: String, courtId: String) = viewModelScope.launch {
        _loadingState.value = UiState.Loading
        // Get Sport Center information and Courts reviews in parallel
        val sportCenterDeferred = async { sportCenterRepository.getSportCenterById(sportCenterId) }
        val reviewsDeferred = async { reservationRepository.getCourtReviews(courtId) }
        val sportCenterState = sportCenterDeferred.await()
        val reviewsState = reviewsDeferred.await()
        // Check sport center result
        if (sportCenterState is UiState.Failure) {
            _loadingState.value = sportCenterState
            return@launch
        }
        _sportCenter = (sportCenterState as UiState.Success).result
        _court = _sportCenter.courts.first { it.id == courtId }
        // Check reviews result
        if (reviewsState is UiState.Failure) {
            _loadingState.value = reviewsState
            return@launch
        }
        _courtReviews = (reviewsState as UiState.Success).result
        // For each reviews get user information
        val deferredUsers = _courtReviews.map { review ->
            async {
                userRepository.getUserInformationById(review.userId)
            }
        }
        val usersResults = deferredUsers.awaitAll()
        for ((index, state) in usersResults.withIndex()) {
            when (state) {
                is UiState.Success -> {
                    _userInformationMap[_courtReviews[index].userId] = state.result
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
