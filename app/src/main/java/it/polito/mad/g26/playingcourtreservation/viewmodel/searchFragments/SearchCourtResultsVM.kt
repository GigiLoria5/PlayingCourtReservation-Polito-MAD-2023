package it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import it.polito.mad.g26.playingcourtreservation.model.Service
import it.polito.mad.g26.playingcourtreservation.model.Sport
import it.polito.mad.g26.playingcourtreservation.model.SportCenter
import it.polito.mad.g26.playingcourtreservation.repository.ServiceRepository
import it.polito.mad.g26.playingcourtreservation.repository.SportCenterRepository
import it.polito.mad.g26.playingcourtreservation.repository.SportRepository

class SearchCourtResultsVM(application: Application) : AndroidViewModel(application) {

    private val sportCenterRepository = SportCenterRepository(application)
    private val serviceRepository = ServiceRepository(application)
    private val sportRepository = SportRepository(application)

    //qui ci metto tutti i servizi
    val services: LiveData<List<Service>> = serviceRepository.services()

    //qui ci metto tutti gli sport
    val sports: LiveData<List<Sport>> = sportRepository.sports()

    //qui ci metto gli sport centers
    val sportCenters: LiveData<List<SportCenter>> = sportCenterRepository.sportCenters()

    //sport selezionato
    private val selectedSport = MutableLiveData(0)


    //qui ci metto gli id dei servizi selezionati. sempre come livedata perchè poi serve nello switch map dei courts
    private var selectedServices = MutableLiveData<MutableSet<Int>>().also {
        it.value = mutableSetOf()
    }


    private val _selectedDateTimeMillis = MutableLiveData<Long>().also {
        it.value = 0
    }
    val selectedDateTimeMillis: LiveData<Long> = _selectedDateTimeMillis
    fun changeSelectedDateTimeMillis(newTimeInMillis: Long) {
        _selectedDateTimeMillis.value = newTimeInMillis
    }

    fun addService(serviceId: Int) {
        selectedServices.value?.add(serviceId)
    }

    fun removeService(serviceId: Int) {
        selectedServices.value?.remove(serviceId)
    }

    fun isServiceInList(serviceId: Int): Boolean {
        return selectedServices.value?.contains(serviceId) ?: false
    }

    fun selectedSportChanged(sportId: Int) {
        selectedSport.value = sportId
    }
    fun getSelectedSportId():Int=selectedSport.value?:0

    //x la lista dei court e dei campi devi provare a fare un switchMap che prenda tutti i parametri di ricerca
}