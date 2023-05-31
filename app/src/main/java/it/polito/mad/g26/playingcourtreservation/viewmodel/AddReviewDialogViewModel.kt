package it.polito.mad.g26.playingcourtreservation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.g26.playingcourtreservation.newModel.Review
import it.polito.mad.g26.playingcourtreservation.newRepository.ReservationRepository
import it.polito.mad.g26.playingcourtreservation.util.UiState
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddReviewDialogViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository
) : ViewModel() {

    private var _review = MutableLiveData<UiState<Review?>>()
    val review: LiveData<UiState<Review?>>
        get() = _review

    fun findReservationReview(reservationId: String, userId: String) = viewModelScope.launch {
        _review.value = UiState.Loading
        val state = reservationRepository.getReservationReviewByUserId(reservationId, userId)
        _review.value = state
    }

    fun addReview(reservationId: String, review: Review) = viewModelScope.launch {
        _review.value = UiState.Loading
        val state = reservationRepository.saveReview(reservationId, review)
        if (state is UiState.Failure) {
            _review.value = state
            return@launch
        }
        findReservationReview(reservationId, review.userId) // to update the review
    }

    fun updateReview(reservationId: String, review: Review) = viewModelScope.launch {
        _review.value = UiState.Loading
        val state = reservationRepository.updateReview(reservationId, review)
        if (state is UiState.Failure) {
            _review.value = state
            return@launch
        }
        findReservationReview(reservationId, review.userId) // to update the review
    }

}
