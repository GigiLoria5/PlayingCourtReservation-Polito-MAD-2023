package it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap

import it.polito.mad.g26.playingcourtreservation.repository.SportCenterRepository
import java.util.*

class SearchCourtActionVM(application: Application) : AndroidViewModel(application) {

    private val sportCenterRepository = SportCenterRepository(application)

    private val searchStringLiveData = MutableLiveData<String>("")
    val cities: LiveData<List<String>> = searchStringLiveData.switchMap { string ->
        val adjustedString = string.trim().lowercase(Locale.ROOT)
        if (TextUtils.isEmpty(adjustedString)) {
            sportCenterRepository.findCities("") //TODO TROVARE MODO DI NON FARE RICERCA IN DB IN QUESTO CASO
        } else {
            sportCenterRepository.findCities("${adjustedString}%")
        }
    }

    fun searchNameChanged(name: String) {
        searchStringLiveData.value = name
    }
}