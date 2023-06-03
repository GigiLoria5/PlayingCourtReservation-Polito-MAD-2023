package it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.g26.playingcourtreservation.repository.SportCenterRepository
import it.polito.mad.g26.playingcourtreservation.util.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchCitiesViewModel @Inject constructor(
    private val sportCenterRepository: SportCenterRepository
) : ViewModel() {

    private val _cities = MutableLiveData<UiState<List<String>>>()
    val cities: LiveData<UiState<List<String>>>
        get() = _cities

    fun getCities(cityNamePrefix: String) = viewModelScope.launch {
        _cities.value = UiState.Loading
        delay(500)
        _cities.value =
            if (cityNamePrefix.isEmpty())
                sportCenterRepository.getAllSportCentersCities()
            else
                sportCenterRepository.getFilteredSportCentersCities(cityNamePrefix)

    }

}
