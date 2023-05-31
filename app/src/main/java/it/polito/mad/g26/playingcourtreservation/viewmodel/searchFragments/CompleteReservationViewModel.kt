package it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.g26.playingcourtreservation.model.Reservation
import it.polito.mad.g26.playingcourtreservation.model.Service
import it.polito.mad.g26.playingcourtreservation.repository.ReservationRepository
import it.polito.mad.g26.playingcourtreservation.repository.SportCenterRepository
import it.polito.mad.g26.playingcourtreservation.repository.UserRepository
import it.polito.mad.g26.playingcourtreservation.util.SearchSportCentersUtils
import it.polito.mad.g26.playingcourtreservation.util.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompleteReservationViewModel @Inject constructor(
    private val sportCenterRepository: SportCenterRepository,
    private val reservationRepository: ReservationRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private var sportCenterId: String = ""
    private var courtId: String = ""
    private var dateTime: Long = 0
    private var _totalAmount = MutableLiveData(0f)
    val totalAmount: LiveData<Float>
        get() = _totalAmount

    /* INITIALIZATION */
    fun initialize(sportCenterId: String, courtId: String, courtHourCharge: Float, dateTime: Long) {
        this.sportCenterId = sportCenterId
        this.courtId = courtId
        _totalAmount.value = courtHourCharge
        this.dateTime = dateTime
    }

    /*DATE TIME MANAGEMENT*/
    private val dateFormat = Reservation.getDatePattern()
    private val timeFormat = Reservation.getTimePattern()

    /*SERVICES MANAGEMENT*/
    private var _services = MutableLiveData<UiState<List<Service>>>()
    val services: LiveData<UiState<List<Service>>>
        get() = _services

    fun getServices() = viewModelScope.launch {
        _services.value = UiState.Loading
        val result = sportCenterRepository.getSportCenterServices(sportCenterId)
        delay(500)
        _services.value = result
    }

    private var selectedServices = MutableLiveData<MutableSet<String>>().also {
        it.value = mutableSetOf()
    }

    fun addSelectedService(service: Service) {
        selectedServices.value?.add(service.name)
        _totalAmount.value = _totalAmount.value?.plus(service.fee)
    }

    fun removeSelectedService(service: Service) {
        selectedServices.value?.remove(service.name)
        _totalAmount.value = _totalAmount.value?.minus(service.fee)
    }

    fun isServiceInList(service: Service): Boolean {
        return selectedServices.value?.contains(service.name) ?: false
    }

    /* COMPLETE RESERVATION*/
    private val _reserveCourtState = MutableLiveData<UiState<Unit>>()
    val reserveCourtState: LiveData<UiState<Unit>>
        get() = _reserveCourtState

    fun reserveCourt() = viewModelScope.launch {
        _reserveCourtState.value = UiState.Loading
        val date = SearchSportCentersUtils.getDateTimeFormatted(
            dateTime,
            dateFormat
        )
        val time = SearchSportCentersUtils.getDateTimeFormatted(
            dateTime,
            timeFormat
        )
        val reservation = Reservation(
            id = Reservation.generateId(courtId, date, time),
            userId = userRepository.currentUser!!.uid,
            sportCenterId = sportCenterId,
            courtId = courtId,
            date = date,
            time = time,
            amount = totalAmount.value!!,
            services = selectedServices.value!!.toList()
        )
        val result = reservationRepository.saveReservation(reservation)
        delay(750)
        _reserveCourtState.value = result
    }
}