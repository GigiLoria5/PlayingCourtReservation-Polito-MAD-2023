package it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments

import android.app.Application
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.lifecycle.*
import it.polito.mad.g26.playingcourtreservation.model.Service
import it.polito.mad.g26.playingcourtreservation.model.Sport
import it.polito.mad.g26.playingcourtreservation.model.custom.SportCenterServicesCourts
import it.polito.mad.g26.playingcourtreservation.repository.ServiceRepository
import it.polito.mad.g26.playingcourtreservation.repository.SportCenterRepository
import it.polito.mad.g26.playingcourtreservation.repository.SportRepository
import java.util.*

class SearchCourtResultsVM(application: Application) : AndroidViewModel(application) {

    private val sportCenterRepository = SportCenterRepository(application)
    private val serviceRepository = ServiceRepository(application)
    private val sportRepository = SportRepository(application)

    /*CITY MANAGEMENT*/
    private lateinit var selectedCity: String
    fun setCity(city: String) {
        selectedCity = city
    }

    /*DATE TIME MANAGEMENT*/
    private val _selectedDateTimeMillis = MutableLiveData<Long>().also {
        it.value = 0
    }
    val selectedDateTimeMillis: LiveData<Long> = _selectedDateTimeMillis
    fun changeSelectedDateTimeMillis(newTimeInMillis: Long) {
        _selectedDateTimeMillis.value = newTimeInMillis
    }

    private fun getHourFormat(): String {
        val c = Calendar.getInstance()
        c.timeInMillis = selectedDateTimeMillis.value!!
        var hourFormatted = SimpleDateFormat("kk:mm", Locale.ITALY).format(c.time)
        if (hourFormatted == "24:00") hourFormatted = "00:00"
        return hourFormatted
    }

    /*SPORT MANAGEMENT*/
    val sports: LiveData<List<Sport>> = sportRepository.sports()
    private val selectedSport = MutableLiveData(0)
    fun selectedSportChanged(sportId: Int) {
        selectedSport.value = sportId
    }

    fun getSelectedSportId(): Int = selectedSport.value ?: 0

    /*SERVICES MANAGEMENT*/
    val services: LiveData<List<Service>> = serviceRepository.services()
    private var selectedServices = MutableLiveData<MutableSet<Int>>().also {
        it.value = mutableSetOf()
    }

    fun addServiceId(serviceId: Int) {
        val s = selectedServices.value
        s?.add(serviceId)
        selectedServices.value = s
    }

    fun removeServiceId(serviceId: Int) {
        val s = selectedServices.value
        s?.remove(serviceId)
        selectedServices.value = s
    }

    fun isServiceIdInList(serviceId: Int): Boolean {
        return selectedServices.value?.contains(serviceId) ?: false
    }

    /*SPORT CENTERS MANAGEMENT*/
    private val sportCentersMediator = MediatorLiveData<Int>()
    val sportCenters = sportCentersMediator.switchMap {
        searchCombinations()
    }

    private fun searchCombinations(): LiveData<List<SportCenterServicesCourts>> {
        return when {
            (selectedSport.value != 0 && selectedServices.value?.isNotEmpty() == true) ->
                sportCenterRepository.filteredSportCentersServicesAndSport(
                    selectedCity,
                    getHourFormat(),
                    selectedServices.value?.toSet() ?: setOf(),
                    selectedSport.value ?: 0
                )
            (selectedSport.value != 0) ->
                sportCenterRepository.filteredSportCentersSportId(
                    selectedCity,
                    getHourFormat(),
                    selectedSport.value ?: 0
                )
            (selectedServices.value?.isNotEmpty() == true) ->
                sportCenterRepository.filteredSportCentersServices(
                    selectedCity,
                    getHourFormat(),
                    selectedServices.value?.toSet() ?: setOf()
                )
            else -> sportCenterRepository.filteredSportCentersBase(selectedCity, getHourFormat())
        }
    }

    /*CHANGES IN SEARCH PARAMETERS MANAGEMENT*/
    init {
        sportCentersMediator.addSource(selectedDateTimeMillis) {
            sportCentersMediator.value = 1
        }
        sportCentersMediator.addSource(selectedSport) {
            sportCentersMediator.value = 2
        }
        sportCentersMediator.addSource(selectedServices) {
            sportCentersMediator.value = 3
        }
    }
    //nell'adapter dello sportcenter, nella funzione che genera i figli, fai una chiamata a qualche dao, magari anche con una funzione da sto vm
    //nb i campi che si vedono dipendono dallo sport selezionato.
    // poi quando fa update dei dati dello sportcenter, aggiorna anche i campi che si vedono
}