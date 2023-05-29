package it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.g26.playingcourtreservation.newModel.Reservation
import it.polito.mad.g26.playingcourtreservation.newModel.Review
import it.polito.mad.g26.playingcourtreservation.newModel.SportCenter
import it.polito.mad.g26.playingcourtreservation.newRepository.ReservationRepository
import it.polito.mad.g26.playingcourtreservation.newRepository.SportCenterRepository
import it.polito.mad.g26.playingcourtreservation.newRepository.UserRepository
import it.polito.mad.g26.playingcourtreservation.util.SearchSportCentersUtil
import it.polito.mad.g26.playingcourtreservation.util.UiState
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchSportCentersViewModel @Inject constructor(
    private val sportCenterRepository: SportCenterRepository,
    private val reservationRepository: ReservationRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    /* INITIALIZATION */
    fun initialize(
        city: String,
        dateTime: Long,
        sportName: String,
        selectedServicesNames: Array<String>
    ) {
        if (
            _selectedDateTimeMillis.value!!.toInt() == 0
        ) {
            setCity(city)
            changeSelectedDateTimeMillis(dateTime)
            changeSelectedSport(sportName)
            selectedServicesNames.forEach { addServiceIdToFilters(it) }
        }
    }

    /* EXISTING RESERVATION MANAGEMENT */
    private val _existingReservationIdByDateAndTime = MutableLiveData<UiState<String?>>()
    val existingReservationIdByDateAndTime: LiveData<UiState<String?>>
        get() = _existingReservationIdByDateAndTime

    fun checkExistingReservation() = viewModelScope.launch {
        _existingReservationIdByDateAndTime.value = UiState.Loading
        val result = reservationRepository.getUserReservationAt(
            userRepository.currentUser!!.uid,
            getDateTimeFormatted(dateFormat),
            getDateTimeFormatted(timeFormat)
        )
        _existingReservationIdByDateAndTime.value = result
    }

    // Load every information needed
    private val _loadingState = MutableLiveData<UiState<Unit>>()
    val loadingState: LiveData<UiState<Unit>>
        get() = _loadingState
    private var sportCenters: List<SportCenter> = listOf()
    var sports: List<String> = listOf()
    var services: List<String> = listOf()
    var reviews: HashMap<String, List<Review>> = hashMapOf()

    fun fetchSportCentersData() = viewModelScope.launch {
        _loadingState.value = UiState.Loading
        // Get all sport centers
        val allSportCentersState = sportCenterRepository.getAllSportCenters()
        if (allSportCentersState is UiState.Failure) {
            _loadingState.value = allSportCentersState
            return@launch
        }
        val allSportCenters = (allSportCentersState as UiState.Success).result
        // Find all sports and services available
        sports = allSportCenters
            .flatMap { it.courts.map { court -> court.sport } }
            .distinct()
        services = allSportCenters
            .flatMap { it.services.map { service -> service.name } }
            .distinct()
        // Filter sport centers based on current city selected
        sportCenters = allSportCenters.filter { it.city == selectedCity }.sortedBy { it.name }
        // Get all reviews for each sport center
        val deferredReviews = allSportCenters.map { sportCenter ->
            async {
                reservationRepository.getAllSportCenterReviews(sportCenter)
            }
        }
        val reviewsResults = deferredReviews.awaitAll()
        for ((index, state) in reviewsResults.withIndex()) {
            when (state) {
                is UiState.Success -> {
                    reviews[allSportCenters[index].id] = state.result
                }

                is UiState.Failure -> {
                    _loadingState.value = state
                }

                else -> {
                    _loadingState.value = UiState.Failure(null)
                }
            }
        }
        _loadingState.value = UiState.Success(Unit)
    }

    /*CITY MANAGEMENT*/
    private lateinit var selectedCity: String
    private fun setCity(city: String) {
        selectedCity = city
    }

    /*DATE TIME MANAGEMENT*/
    private val dateFormat = Reservation.getDatePattern()
    private val timeFormat = Reservation.getTimePattern()
    private val _selectedDateTimeMillis = MutableLiveData<Long>().also {
        it.value = 0
    }

    val selectedDateTimeMillis: LiveData<Long> = _selectedDateTimeMillis

    fun changeSelectedDateTimeMillis(newTimeInMillis: Long) {
        _selectedDateTimeMillis.value =
            if (newTimeInMillis >= SearchSportCentersUtil.getMockInitialDateTime())
                newTimeInMillis
            else
                SearchSportCentersUtil.getMockInitialDateTime()
    }

    private fun getDateTimeFormatted(format: String): String {
        return SearchSportCentersUtil.getDateTimeFormatted(
            selectedDateTimeMillis.value ?: 0,
            format
        )
    }

    /*SPORT MANAGEMENT*/
    private val selectedSport = MutableLiveData("")
    fun changeSelectedSport(sportName: String) {
        selectedSport.value = sportName
    }

    fun getSelectedSportName(): String = selectedSport.value ?: ""

    /* VM LIVE DATA CHAIN:
    * selectedDateTimeMillis -> existingReservationIdByDateAndTime -> services
    *                                                              -> sportCenters
    * */


    /*SERVICES MANAGEMENT*/
    private var selectedServices = MutableLiveData<MutableSet<String>>().also {
        it.value = mutableSetOf()
    }

    fun addServiceIdToFilters(serviceName: String) {
        val s = selectedServices.value
        s?.add(serviceName)
        selectedServices.value = s
    }

    fun removeServiceIdFromFilters(serviceName: String) {
        val s = selectedServices.value
        s?.remove(serviceName)
        selectedServices.value = s
    }

    fun isServiceNameInList(serviceName: String): Boolean {
        return selectedServices.value?.contains(serviceName) ?: false
    }

    fun getSelectedServices(): Array<String> = selectedServices.value?.toTypedArray() ?: arrayOf()

    /*SPORT CENTERS MANAGEMENT*/
    fun getSportCentersWithDetailsFormatted(): List<SportCenter> {
        val selectedServices = selectedServices.value?.toSet() ?: setOf()
        val sportName = selectedSport.value ?: ""

        val filterBySportName = { sportCenter: SportCenter ->
            sportCenter.courts.any { court ->
                court.sport == sportName
            }
        }
        val filterBySelectedServices = { sportCenter: SportCenter ->
            selectedServices.all { selectedService ->
                sportCenter.services.any { service ->
                    service.name == selectedService
                }
            }
        }
        return when {
            sportCenters.isEmpty() -> sportCenters

            /* FILTERING BY SPORT, SERVICES AND BASE (DATE,TIME,CITY)*/
            (sportName != "" && selectedServices.isNotEmpty()) ->
                sportCenters.filter { sportCenter ->
                    filterBySportName(sportCenter) && filterBySelectedServices(sportCenter)
                }

            /* FILTERING BY SPORT AND BASE (DATE,TIME,CITY)*/
            (sportName != "") ->
                sportCenters.filter { sportCenter ->
                    filterBySportName(sportCenter)
                }

            /* FILTERING BY SERVICES AND BASE (DATE,TIME,CITY)*/
            (selectedServices.isNotEmpty()) ->
                sportCenters.filter { sportCenter ->
                    filterBySelectedServices(sportCenter)
                }

            /* BASE FILTERING (DATE,TIME,CITY) */
            else -> sportCenters
        }
    }

}
