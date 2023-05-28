package it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.g26.playingcourtreservation.newModel.SportCenter
import it.polito.mad.g26.playingcourtreservation.newRepository.SportCenterRepository
import it.polito.mad.g26.playingcourtreservation.util.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchSportCentersViewModel @Inject constructor(
    private val sportCenterRepository: SportCenterRepository
) : ViewModel() {

    private val _sportCenters = MutableLiveData<UiState<List<SportCenter>>>()
    val sportCenters: LiveData<UiState<List<SportCenter>>>
        get() = _sportCenters

    fun getSportCenters() = viewModelScope.launch {
        _sportCenters.value = UiState.Loading
        delay(500)
        _sportCenters.value = sportCenterRepository.getSportCenters()
    }

}
