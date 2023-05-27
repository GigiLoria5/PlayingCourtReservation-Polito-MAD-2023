package it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.g26.playingcourtreservation.newModel.SportCenter
import it.polito.mad.g26.playingcourtreservation.newRepository.SportCenterRepository
import javax.inject.Inject

@HiltViewModel
class SearchSportCentersViewModel @Inject constructor(
    private val sportCenterRepository: SportCenterRepository
) : ViewModel() {
    private val _sportCenters = MutableLiveData<List<SportCenter>>()
    val sportCenters: LiveData<List<SportCenter>>
        get() = _sportCenters

    fun getSportCenters() {
        _sportCenters.value = sportCenterRepository.getSportCenters()
    }

    fun getSportCentersCities(): List<String> {
        return sportCenterRepository.getSportCentersCities()
    }
}
