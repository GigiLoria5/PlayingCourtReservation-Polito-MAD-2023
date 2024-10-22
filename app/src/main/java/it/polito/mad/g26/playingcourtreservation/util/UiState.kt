package it.polito.mad.g26.playingcourtreservation.util

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<out T>(val result: T) : UiState<T>()
    data class Failure(val error: String?) : UiState<Nothing>()
}
