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
import kotlinx.coroutines.delay
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
    private val _existingReservationByDateAndTime = MutableLiveData<UiState<Reservation?>>()
    val existingReservationByDateAndTime: LiveData<UiState<Reservation?>>
        get() = _existingReservationByDateAndTime

    fun checkExistingReservation() = viewModelScope.launch {
        _existingReservationByDateAndTime.value = UiState.Loading
        val result = reservationRepository.getUserReservationAt(
            userRepository.currentUser!!.uid,
            getDateTimeFormatted(dateFormat),
            getDateTimeFormatted(timeFormat)
        )
        _existingReservationByDateAndTime.value = result
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
        val deferredReviews = sportCenters.map { sportCenter ->
            async {
                reservationRepository.getAllSportCenterReviews(sportCenter)
            }
        }
        val reviewsResults = deferredReviews.awaitAll()
        for ((index, state) in reviewsResults.withIndex()) {
            when (state) {
                is UiState.Success -> {
                    reviews[sportCenters[index].id] = state.result
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
    private val _selectedSport = MutableLiveData("")
    fun changeSelectedSport(sportName: String) {
        _selectedSport.value = sportName
    }

    fun getSelectedSportName(): String = _selectedSport.value ?: ""

    /* VM LIVE DATA CHAIN:
    * selectedDateTimeMillis -> existingReservationIdByDateAndTime -> services
    *                                                              -> sportCenters
    * */

    /*SERVICES MANAGEMENT*/
    private var _selectedServices = MutableLiveData<MutableSet<String>>().also {
        it.value = mutableSetOf()
    }

    fun getSelectedServices(): Array<String> = _selectedServices.value?.toTypedArray() ?: arrayOf()

    fun addServiceIdToFilters(serviceName: String) {
        val s = _selectedServices.value
        s?.add(serviceName)
        _selectedServices.value = s
    }

    fun removeServiceIdFromFilters(serviceName: String) {
        val s = _selectedServices.value
        s?.remove(serviceName)
        _selectedServices.value = s
    }

    fun isServiceNameInList(serviceName: String): Boolean {
        return _selectedServices.value?.contains(serviceName) ?: false
    }

    /*SPORT CENTERS MANAGEMENT*/
    init {
        _selectedSport.observeForever {
            updateSportCentersUI()
        }

        _selectedServices.observeForever {
            updateSportCentersUI()
        }

        _selectedDateTimeMillis.observeForever {
            updateSportCentersUI()
        }
    }

    private fun updateSportCentersUI() = viewModelScope.launch {
        // If is not in Success state it means we are still processing the data
        if (_loadingState.value !is UiState.Success) {
            return@launch
        }
        _loadingState.value = UiState.Loading
        delay(500)
        _loadingState.value = UiState.Success(Unit)
    }

    fun getFilteredSportCenters(): List<SportCenter> {
        val selectedTime = getDateTimeFormatted(timeFormat)
        val selectedServices = _selectedServices.value.orEmpty().toSet()
        val selectedSport = _selectedSport.value.orEmpty()

        return sportCenters
            .filter { sportCenter ->
                sportCenter.openTime <= selectedTime && selectedTime < sportCenter.closeTime
            }
            .filter { sportCenter ->
                selectedSport == "All sports" || selectedSport.isEmpty() || sportCenter.courts.any { it.sport == selectedSport }
            }
            .filter { sportCenter ->
                selectedServices.isEmpty() || selectedServices.all { selectedService ->
                    sportCenter.services.any { it.name == selectedService }
                }
            }
    }

}
