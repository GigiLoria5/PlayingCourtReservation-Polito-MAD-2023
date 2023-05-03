package it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import it.polito.mad.g26.playingcourtreservation.model.Service
import it.polito.mad.g26.playingcourtreservation.repository.ServiceRepository
import it.polito.mad.g26.playingcourtreservation.repository.SportCenterRepository

class SearchCourtResultsVM(application: Application) : AndroidViewModel(application) {

    private val sportCenterRepository = SportCenterRepository(application)
    private val serviceRepository = ServiceRepository(application)

    //qui ci metto tutti i servizi
    val services: LiveData<List<Service>> = serviceRepository.services()


    //qui ci metto gli id dei servizi selezionati. sempre come livedata perchè poi serve nello switch map dei courts
    private var selectedServices = MutableLiveData<MutableSet<Int>>().also {
        it.value= mutableSetOf()
    }


    private val _selectedDateTimeMillis = MutableLiveData<Long>().also {
        it.value = 0;
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
}