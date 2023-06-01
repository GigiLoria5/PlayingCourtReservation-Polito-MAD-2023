package it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.g26.playingcourtreservation.model.Reservation
import it.polito.mad.g26.playingcourtreservation.model.Review
import it.polito.mad.g26.playingcourtreservation.model.SportCenter
import it.polito.mad.g26.playingcourtreservation.repository.ReservationRepository
import it.polito.mad.g26.playingcourtreservation.repository.SportCenterRepository
import it.polito.mad.g26.playingcourtreservation.repository.UserRepository
import it.polito.mad.g26.playingcourtreservation.util.SearchSportCentersUtils
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

        _selectedSport.observeForever {
            fetchData()
        }

        _selectedServices.observeForever {
            fetchData()
        }

        _selectedDateTimeMillis.observeForever {
            fetchData()
        }
    }

    // Load every information needed
    private val _loadingState = MutableLiveData<UiState<Unit>>()
    val loadingState: LiveData<UiState<Unit>>
        get() = _loadingState

    private var _reservation: Reservation? = null
    val reservation: Reservation?
        get() = _reservation

    private var allCitySportCenters: List<SportCenter> = listOf()
    // The get method is the function "getFilteredSportCenters"

    private var _allSports: List<String> = listOf()
    val allSports: List<String>
        get() = _allSports

    private var _allServices: List<String> = listOf()
    val allServices: List<String>
        get() = _allServices

    private var _sportCentersReviews: HashMap<String, List<Review>> = hashMapOf()
    val sportCenterReviews: HashMap<String, List<Review>>
        get() = _sportCentersReviews

    fun fetchData() = viewModelScope.launch {
        if (_loadingState.value is UiState.Loading)
            return@launch // Avoid multiple calls
        _loadingState.value = UiState.Loading
        // Search actual reservation and sport centers in parallel
        val reservationSearchDeferred = async {
            reservationRepository.getUserReservationAt(
                userRepository.currentUser!!.uid,
                getDateTimeFormatted(dateFormat),
                getDateTimeFormatted(timeFormat)
            )
        }
        val sportCentersSearchDeferred = async {
            sportCenterRepository.getAllSportCenters()
        }
        // Wait for reservation search result
        val reservationSearchState = reservationSearchDeferred.await()
        // If the reservation search failed or has found one, we don't do any more db call
        if (reservationSearchState is UiState.Failure) {
            sportCentersSearchDeferred.cancel()
            _loadingState.value = reservationSearchState
            return@launch
        }
        if (reservationSearchState is UiState.Success && reservationSearchState.result != null) {
            sportCentersSearchDeferred.cancel()
            _reservation = reservationSearchState.result
            _loadingState.value = UiState.Success(Unit)
            return@launch
        }
        // Otherwise, if there is no already a reservation, we get/wait all the other information needed
        _reservation = null
        val allSportCentersState = sportCentersSearchDeferred.await()
        if (allSportCentersState is UiState.Failure) {
            _loadingState.value = allSportCentersState
            return@launch
        }
        val allSportCenters = (allSportCentersState as UiState.Success).result
        allCitySportCenters = allSportCenters
            .filter { it.city == selectedCity } // Filter sport centers based on current city selected
            .sortedBy { it.name }
        val deferredReviews = allCitySportCenters.map { sportCenter ->
            async {
                reservationRepository.getAllSportCenterReviews(sportCenter)
            }
        }
        // Find all sports and services available
        _allSports = allSportCenters
            .flatMap { it.courts.map { court -> court.sport } }
            .distinct()
        _allServices = allSportCenters
            .flatMap { it.services.map { service -> service.name } }
            .distinct()
        // Get all reviews for each sport center
        val reviewsResults = deferredReviews.awaitAll()
        for ((index, state) in reviewsResults.withIndex()) {
            when (state) {
                is UiState.Success -> {
                    _sportCentersReviews[allCitySportCenters[index].id] = state.result
                }

                is UiState.Failure -> {
                    _loadingState.value = state
                }

                else -> {
                    _loadingState.value = UiState.Failure("Unable to fetch data at the moment")
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
            if (newTimeInMillis >= SearchSportCentersUtils.getMockInitialDateTime())
                newTimeInMillis
            else
                SearchSportCentersUtils.getMockInitialDateTime()
    }

    private fun getDateTimeFormatted(format: String): String {
        return SearchSportCentersUtils.getDateTimeFormatted(
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
    fun getFilteredSportCenters(): List<SportCenter> {
        val selectedTime = getDateTimeFormatted(timeFormat)
        val selectedServices = _selectedServices.value.orEmpty().toSet()
        val selectedSport = _selectedSport.value.orEmpty()

        return allCitySportCenters
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
