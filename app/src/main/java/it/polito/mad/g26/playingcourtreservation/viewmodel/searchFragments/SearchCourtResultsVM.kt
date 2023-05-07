package it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments

import android.app.Application
import android.icu.util.Calendar
import androidx.lifecycle.*
import it.polito.mad.g26.playingcourtreservation.model.Reservation
import it.polito.mad.g26.playingcourtreservation.model.Service
import it.polito.mad.g26.playingcourtreservation.model.Sport
import it.polito.mad.g26.playingcourtreservation.model.custom.SportCenterServicesCourts
import it.polito.mad.g26.playingcourtreservation.repository.ReservationRepository
import it.polito.mad.g26.playingcourtreservation.repository.ServiceRepository
import it.polito.mad.g26.playingcourtreservation.repository.SportCenterRepository
import it.polito.mad.g26.playingcourtreservation.repository.SportRepository
import it.polito.mad.g26.playingcourtreservation.util.SearchCourtResultsUtil
import it.polito.mad.g26.playingcourtreservation.enums.CourtStatus
import it.polito.mad.g26.playingcourtreservation.model.custom.ServiceWithFee
import it.polito.mad.g26.playingcourtreservation.model.custom.SportCenterWithDataFormatted

class SearchCourtResultsVM(application: Application) : AndroidViewModel(application) {

    private val sportCenterRepository = SportCenterRepository(application)
    private val serviceRepository = ServiceRepository(application)
    private val sportRepository = SportRepository(application)
    private val reservationRepository = ReservationRepository(application)

    private val searchResultUtils = SearchCourtResultsUtil

    /*CITY MANAGEMENT*/
    private lateinit var selectedCity: String
    fun setCity(city: String) {
        selectedCity = city
    }

    /*DATE TIME MANAGEMENT*/
    private val _selectedDateTimeMillis = MutableLiveData<Long>().also {
        it.value = searchResultUtils.getMockInitialDateTime().timeInMillis

    }
    val selectedDateTimeMillis: LiveData<Long> = _selectedDateTimeMillis
    fun changeSelectedDateTimeMillis(newTimeInMillis: Long) {
        _selectedDateTimeMillis.value = newTimeInMillis
    }

    private fun getDateTimeFormatted(format: String): String {
        val c = Calendar.getInstance()
        c.timeInMillis = selectedDateTimeMillis.value!!
        return searchResultUtils.getDateTimeFormatted(c, format)
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

    fun addServiceIdToFilters(serviceId: Int) {
        val s = selectedServices.value
        s?.add(serviceId)
        selectedServices.value = s
    }

    fun removeServiceIdFromFilters(serviceId: Int) {
        val s = selectedServices.value
        s?.remove(serviceId)
        selectedServices.value = s
        selectedServicesPerSportCenter.forEach { (t, _) ->
            removeServiceSelectionFromSportCenter(t, serviceId)
        }
    }


    fun isServiceIdInList(serviceId: Int): Boolean {
        return selectedServices.value?.contains(serviceId) ?: false
    }

    /*SPORT CENTERS MANAGEMENT*/
    private val sportCentersMediator = MediatorLiveData<Int>()
    val sportCenters = sportCentersMediator.switchMap {
        searchCombinations()
    }
    private var _sportCentersCount = sportCenters.switchMap {
        MutableLiveData(sportCenters.value?.size ?: 0)
    }
    val sportCentersCount: LiveData<Int> = _sportCentersCount

    private fun searchCombinations(): LiveData<List<SportCenterServicesCourts>> {
        return when {
            (selectedSport.value != 0 && selectedServices.value?.isNotEmpty() == true) ->
                sportCenterRepository.filteredSportCentersServicesAndSport(
                    selectedCity,
                    getDateTimeFormatted("kk:mm"),
                    selectedServices.value?.toSet() ?: setOf(),
                    selectedSport.value ?: 0
                )
            (selectedSport.value != 0) ->
                sportCenterRepository.filteredSportCentersSportId(
                    selectedCity,
                    getDateTimeFormatted("kk:mm"),
                    selectedSport.value ?: 0
                )
            (selectedServices.value?.isNotEmpty() == true) ->
                sportCenterRepository.filteredSportCentersServices(
                    selectedCity,
                    getDateTimeFormatted("kk:mm"),
                    selectedServices.value?.toSet() ?: setOf()
                )
            else -> sportCenterRepository.filteredSportCentersBase(
                selectedCity,
                getDateTimeFormatted("kk:mm")
            )
        }
    }

    fun getSportCentersWithDataFormatted(): List<SportCenterWithDataFormatted> {
        return sportCenters.value?.map {
            val sportCenter = it.sportCenter
            val courtsWithDetails = it.courtsWithDetails.filter { courtWithDetails ->
                if (getSelectedSportId() != 0)
                    courtWithDetails.sport.id == getSelectedSportId()
                else
                    true
            }
            val servicesWithFee = it.sportCenterServices.mapNotNull { serviceWithFee ->
                val service =
                    services.value?.find { service -> service.id == serviceWithFee.idService }
                if (service != null)
                    ServiceWithFee(service, serviceWithFee.fee)
                else
                    null
            }
            SportCenterWithDataFormatted(sportCenter, courtsWithDetails, servicesWithFee)
        } ?: listOf()
    }

    /* SPORT CENTER SERVICES MANAGEMENT */
    private val selectedServicesPerSportCenter: MutableMap<Int, MutableSet<Int>> = mutableMapOf()

    fun addServiceSelectionToSportCenter(sportCenterId: Int, serviceId: Int) {
        if (selectedServicesPerSportCenter[sportCenterId] != null)
            selectedServicesPerSportCenter[sportCenterId]?.add(serviceId)
        else {
            selectedServicesPerSportCenter[sportCenterId] = mutableSetOf(serviceId)
        }
        selectedServicesPerSportCenter.forEach { (t, u) -> println("${sportCenters.value?.find { it.sportCenter.id == t }?.sportCenter?.name} = $u") }
    }

    fun removeServiceSelectionFromSportCenter(sportCenterId: Int, serviceId: Int) {
        if (selectedServicesPerSportCenter[sportCenterId] != null)
            selectedServicesPerSportCenter[sportCenterId]?.remove(serviceId)
        selectedServicesPerSportCenter.forEach { (t, u) -> println("${sportCenters.value?.find { it.sportCenter.id == t }?.sportCenter?.name} = $u") }
    }

    fun isServiceIdInSelectionList(sportCenterId: Int, serviceId: Int): Boolean {
        return selectedServicesPerSportCenter[sportCenterId]?.contains(serviceId) ?: false
    }

    fun updateSelectedServicesPerSportCenter() {
        //INIT selectedServicesPerSportCenter WITH FILTER CHOICES
        sportCenters.value?.forEach {
            selectedServicesPerSportCenter[it.sportCenter.id] = mutableSetOf()
        }
        selectedServicesPerSportCenter.forEach { (t, _) ->
            selectedServices.value?.forEach { selectedServiceId ->
                if (sportCenters.value?.find { it.sportCenter.id == t }?.sportCenterServices?.any { it.idService == selectedServiceId } == true)
                    addServiceSelectionToSportCenter(t, selectedServiceId)
            }
        }
    }

    /*RESERVATIONS MANAGEMENT*/
    val reservations: LiveData<List<Reservation>> = sportCenters.switchMap {
        val courtsIdList =
            it.flatMap { sportCenterData -> sportCenterData.courtsWithDetails.map { court -> court.court.id } }
        val date = getDateTimeFormatted("dd-MM-YYYY")
        val hour = getDateTimeFormatted("kk:mm")
        reservationRepository.filteredReservations(date, hour, courtsIdList)
    }

    val myReservation: LiveData<Int?> = selectedDateTimeMillis.switchMap {
        reservationRepository.myReservationId( //IMPORTANTE CHE SE HO UNA RESERVATION ESSA SIA INDIPENDENTE DALLA CITTà DOVE CERCO
            1, //andrà sostituito con userId
            getDateTimeFormatted("dd-MM-YYYY"),
            getDateTimeFormatted("kk:mm")
        )
    }


    fun courtReservationState(courtId: Int): CourtStatus {
        //X ORA ABBIAMO SOLO USER CON ID 1.
        return when (reservations.value?.find { it.idCourt == courtId }?.idUser) {
            null -> CourtStatus.AVAILABLE
            else -> CourtStatus.NOT_AVAILABLE
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
}