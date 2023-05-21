package it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import it.polito.mad.g26.playingcourtreservation.model.Reservation
import it.polito.mad.g26.playingcourtreservation.model.Service
import it.polito.mad.g26.playingcourtreservation.model.Sport
import it.polito.mad.g26.playingcourtreservation.model.custom.ServiceWithFee
import it.polito.mad.g26.playingcourtreservation.model.custom.SportCenterReviewsSummary
import it.polito.mad.g26.playingcourtreservation.model.custom.SportCenterWithDetails
import it.polito.mad.g26.playingcourtreservation.model.custom.SportCenterWithMoreDetailsFormatted
import it.polito.mad.g26.playingcourtreservation.repository.ReservationRepository
import it.polito.mad.g26.playingcourtreservation.repository.ReviewRepository
import it.polito.mad.g26.playingcourtreservation.repository.ServiceRepository
import it.polito.mad.g26.playingcourtreservation.repository.SportCenterRepository
import it.polito.mad.g26.playingcourtreservation.repository.SportRepository
import it.polito.mad.g26.playingcourtreservation.util.SearchSportCentersUtil

class SearchSportCentersVM(application: Application) : AndroidViewModel(application) {

    private val sportCenterRepository = SportCenterRepository(application)
    private val serviceRepository = ServiceRepository(application)
    private val sportRepository = SportRepository(application)
    private val reservationRepository = ReservationRepository(application)
    private val reviewRepository = ReviewRepository(application)

    /* INITIALIZATION */
    fun initialize(city: String, dateTime: Long, sportId: Int, selectedServicesIds: IntArray) {

        if (
            _selectedDateTimeMillis.value!!.toInt() == 0
        ) {
            setCity(city)
            changeSelectedDateTimeMillis(dateTime)
            changeSelectedSport(sportId)
            selectedServicesIds.forEach { addServiceIdToFilters(it) }
        }
    }

    /*CITY MANAGEMENT*/
    private lateinit var selectedCity: String
    private fun setCity(city: String) {
        selectedCity = city
    }

    /*DATE TIME MANAGEMENT*/
    private val dateFormat = Reservation.getReservationDatePattern()
    private val timeFormat = Reservation.getReservationTimePattern()
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
    val sports: LiveData<List<Sport>> = sportRepository.sports()
    private val selectedSport = MutableLiveData(0)
    fun changeSelectedSport(sportId: Int) {
        selectedSport.value = sportId
    }

    fun getSelectedSportId(): Int = selectedSport.value ?: 0

    /* VM LIVE DATA CHAIN:
    * selectedDateTimeMillis -> existingReservationIdByDateAndTime -> services
    *                                                              -> sportCenters
    * */

    /*EXISTING RESERVATION MANAGEMENT*/
    val existingReservationIdByDateAndTime: LiveData<Int?> = selectedDateTimeMillis.switchMap {
        reservationRepository.reservationIdByUserIdAndDateAndHour(
            1, //should be replaced with userId
            getDateTimeFormatted(dateFormat),
            getDateTimeFormatted(timeFormat)
        )
    }


    /*SERVICES MANAGEMENT*/
    val services: LiveData<List<Service>> = existingReservationIdByDateAndTime.switchMap {
        if (it == null)
            serviceRepository.services()
        else MutableLiveData<List<Service>>().also { services ->
            services.value = listOf()
        }
    }
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

    fun getSelectedServices(): IntArray = selectedServices.value?.toIntArray() ?: intArrayOf()


    /*SPORT CENTERS MANAGEMENT*/
    val sportCentersMediator = MediatorLiveData<Int>()
    private val sportCenters = existingReservationIdByDateAndTime.switchMap {
        if (it == null)
            sportCenterRepository.filterSportCenters(
                selectedCity,
                getDateTimeFormatted(timeFormat)
            ) else
            MutableLiveData<List<SportCenterWithDetails>>().also { sportCenters ->
                sportCenters.value = listOf()
            }
    }

    private fun SportCenterWithDetails.formatter(): SportCenterWithMoreDetailsFormatted {
        val reviews = reviews.value?.find { review -> review.sportCenterId == sportCenter.id }
            ?: SportCenterReviewsSummary(sportCenter.id, 0.0, 0)

        return SportCenterWithMoreDetailsFormatted(
            sportCenter,
            sportCenterServicesWithDetails.map {
                ServiceWithFee(it.service, it.sportCenterServices.fee)
            },
            reviews,
            courts
        )
    }

    private val reviews = sportCenters.switchMap {
        val sportCentersIds = sportCenters.value?.map { it.sportCenter.id }?.toSet() ?: setOf()
        reviewRepository.reviewsSummariesBySportCentersIds(sportCentersIds)
    }

    fun getSportCentersWithDetailsFormatted(): List<SportCenterWithMoreDetailsFormatted> {
        val selectedServices = selectedServices.value?.toSet() ?: setOf()
        val sportId = selectedSport.value ?: 0
        val sportCentersFormatted = sportCenters.value?.map {
            it.formatter()
        } ?: listOf()
        val filterBySportId = { sportCenter: SportCenterWithMoreDetailsFormatted ->
            sportCenter.courts.any { court ->
                court.sport.id == sportId
            }
        }
        val filterBySelectedServices = { sportCenter: SportCenterWithMoreDetailsFormatted ->
            selectedServices.all { selectedService ->
                sportCenter.servicesWithFee.any { serviceWithFee ->
                    serviceWithFee.service.id == selectedService
                }
            }
        }
        return when {

            sportCentersFormatted.isEmpty() -> sportCentersFormatted

            /* FILTERING BY SPORT, SERVICES AND BASE (DATE,TIME,CITY)*/
            (sportId != 0 && selectedServices.isNotEmpty()) ->
                sportCentersFormatted.filter { sportCenter ->
                    filterBySportId(sportCenter) && filterBySelectedServices(sportCenter)
                }

            /* FILTERING BY SPORT AND BASE (DATE,TIME,CITY)*/
            (sportId != 0) ->
                sportCentersFormatted.filter { sportCenter ->
                    filterBySportId(sportCenter)
                }

            /* FILTERING BY SERVICES AND BASE (DATE,TIME,CITY)*/
            (selectedServices.isNotEmpty()) ->
                sportCentersFormatted.filter { sportCenter ->
                    filterBySelectedServices(sportCenter)
                }

            /* BASE FILTERING (DATE,TIME,CITY) */
            else -> sportCentersFormatted
        }
    }

    /*CHANGES IN SEARCH PARAMETERS MANAGEMENT*/
    init {
        sportCentersMediator.addSource(selectedSport) {
            sportCentersMediator.value = 1
        }
        sportCentersMediator.addSource(selectedServices) {
            sportCentersMediator.value = 2
        }

        //PUT REVIEW INSTEAD OF SPORT CENTER BECAUSE THE REVIEW UPDATE IS SUBSEQUENT
        // TO THE SPORT CENTER UPDATE (SWITCH MAP) AND THE REVIEW INFORMATION IS
        // NEEDED TO CREATE THE FINAL SPORT CENTER OBJECT
        sportCentersMediator.addSource(reviews) {
            sportCentersMediator.value = 3
        }
    }
}