package it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import it.polito.mad.g26.playingcourtreservation.model.Reservation.Companion.getReservationDatePattern
import it.polito.mad.g26.playingcourtreservation.model.Service
import it.polito.mad.g26.playingcourtreservation.model.Sport
import it.polito.mad.g26.playingcourtreservation.model.custom.ServiceWithFee
import it.polito.mad.g26.playingcourtreservation.model.custom.SportCenterWithServices
import it.polito.mad.g26.playingcourtreservation.model.custom.SportCenterWithServicesFormatted
import it.polito.mad.g26.playingcourtreservation.repository.ReservationRepository
import it.polito.mad.g26.playingcourtreservation.repository.ServiceRepository
import it.polito.mad.g26.playingcourtreservation.repository.SportCenterRepository
import it.polito.mad.g26.playingcourtreservation.repository.SportRepository
import it.polito.mad.g26.playingcourtreservation.util.SearchSportCentersUtil

class SearchSportCentersVM(application: Application) : AndroidViewModel(application) {

    private val sportCenterRepository = SportCenterRepository(application)
    private val serviceRepository = ServiceRepository(application)
    private val sportRepository = SportRepository(application)
    private val reservationRepository = ReservationRepository(application)

    private val searchSportCentersUtil = SearchSportCentersUtil

    /*CITY MANAGEMENT*/
    private lateinit var selectedCity: String
    fun setCity(city: String) {
        selectedCity = city
    }

    /*DATE TIME MANAGEMENT*/
    private val dateFormat = getReservationDatePattern()
    private val timeFormat = "kk:mm"
    private val _selectedDateTimeMillis = MutableLiveData<Long>().also {
        it.value = searchSportCentersUtil.getMockInitialDateTime().timeInMillis

    }
    val selectedDateTimeMillis: LiveData<Long> = _selectedDateTimeMillis
    fun changeSelectedDateTimeMillis(newTimeInMillis: Long) {
        _selectedDateTimeMillis.value = newTimeInMillis
    }
    private fun getDateTimeFormatted(format: String): String {
        return searchSportCentersUtil.getDateTimeFormatted(selectedDateTimeMillis.value ?: 0, format)
    }

    /*SPORT MANAGEMENT*/
    val sports: LiveData<List<Sport>> = sportRepository.sports()
    private val selectedSport = MutableLiveData(0)
    fun changeSelectedSport(sportId: Int) {
        selectedSport.value = sportId
    }
    fun getSelectedSportId(): Int = selectedSport.value ?: 0

    /*SERVICES MANAGEMENT*/
    val services: LiveData<List<Service>> = serviceRepository.services()
    private var selectedServices = MutableLiveData<MutableSet<Int>>().also {
        it.value = mutableSetOf()
    }
    fun addServiceIdToFilters(serviceId: Int) {
        val s = selectedServices.value
        s?.add(serviceId)
        selectedServices.value = s
    }
    fun removeServiceIdFromFilters(serviceId: Int) {
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
        sportCenterRepository.filterSportCenters(
            selectedCity,
            getDateTimeFormatted(timeFormat),
            selectedServices.value?.toSet() ?: setOf(),
            selectedSport.value ?: 0
        )
    }
    private fun SportCenterWithServices.formatter(): SportCenterWithServicesFormatted {
        return SportCenterWithServicesFormatted(sportCenter,
            sportCenterServicesWithDetails.map {
                ServiceWithFee(it.service, it.sportCenterServices.fee)
            }
        )
    }
    fun getSportCentersWithServicesFormatted(): List<SportCenterWithServicesFormatted> {
        return sportCenters.value?.map {
            it.formatter()
        } ?: listOf()
    }
    
    fun getNumberOfSportCentersFound():Int=sportCenters.value?.size?:0

    /*RESERVATION MANAGEMENT*/
    val existingReservationIdByDateAndTime: LiveData<Int?> = selectedDateTimeMillis.switchMap {
        reservationRepository.reservationIdByUserIdAndDateAndHour(
            1, //should be replaced with userId
            getDateTimeFormatted(dateFormat),
            getDateTimeFormatted(timeFormat)
        )
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
}