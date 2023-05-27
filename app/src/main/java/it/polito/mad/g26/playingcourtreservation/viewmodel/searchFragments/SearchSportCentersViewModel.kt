package it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.g26.playingcourtreservation.newModel.SportCenter
import it.polito.mad.g26.playingcourtreservation.newRepository.SportCenterRepository
import it.polito.mad.g26.playingcourtreservation.util.UiState
import javax.inject.Inject

@HiltViewModel
class SearchSportCentersViewModel @Inject constructor(
    private val sportCenterRepository: SportCenterRepository
) : ViewModel() {
    private val _sportCenters = MutableLiveData<UiState<List<SportCenter>>>()
    val sportCenters: LiveData<UiState<List<SportCenter>>>
        get() = _sportCenters

    fun getSportCenters() {
        _sportCenters.value = UiState.Loading
        Handler(Looper.getMainLooper()).postDelayed({
            _sportCenters.value = sportCenterRepository.getSportCenters()
        }, 2000)
    }

    fun getSportCentersCities(): UiState<List<String>> {
        return sportCenterRepository.getSportCentersCities()
    }
}
