package it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import it.polito.mad.g26.playingcourtreservation.repository.SportCenterRepository
import java.util.Locale

class SearchCourtActionVM(application: Application) : AndroidViewModel(application) {

    private val sportCenterRepository = SportCenterRepository(application)

    private val searchStringLiveData = MutableLiveData("")
    val cities: LiveData<List<String>> = searchStringLiveData.switchMap { string ->
        val adjustedString = string.trim().lowercase(Locale.ROOT)
        if (TextUtils.isEmpty(adjustedString)) {
            sportCenterRepository.findAllCities()
        } else {
            sportCenterRepository.filteredCities("${adjustedString}%")
        }
    }

    fun searchNameChanged(name: String) {
        searchStringLiveData.value = name
    }
}