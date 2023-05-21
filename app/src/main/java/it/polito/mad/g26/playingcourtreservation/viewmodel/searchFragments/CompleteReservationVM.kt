package it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.room.Transaction
import it.polito.mad.g26.playingcourtreservation.model.Reservation
import it.polito.mad.g26.playingcourtreservation.model.custom.ServiceWithFee
import it.polito.mad.g26.playingcourtreservation.model.custom.SportCenterServicesWithDetails
import it.polito.mad.g26.playingcourtreservation.repository.ReservationRepository
import it.polito.mad.g26.playingcourtreservation.repository.ReservationServiceRepository
import it.polito.mad.g26.playingcourtreservation.repository.SportCenterServicesRepository
import it.polito.mad.g26.playingcourtreservation.util.SearchSportCentersUtil
import kotlin.concurrent.thread

class CompleteReservationVM(application: Application) : AndroidViewModel(application) {

    private val sportCenterServicesRepository = SportCenterServicesRepository(application)
    private val reservationRepository = ReservationRepository(application)
    private val reservationServiceRepository = ReservationServiceRepository(application)

    /* INITIALIZATION */
    fun initialize(sportCenterId: Int, courtId: Int, courtHourCharge: Float, dateTime: Long) {
        setSportCenterId(sportCenterId)
        setCourtId(courtId)
        setTotalAmount(courtHourCharge)
        setDateTime(dateTime)
    }


    /*DATE TIME MANAGEMENT*/
    private val dateFormat = Reservation.getReservationDatePattern()
    private val timeFormat = Reservation.getReservationTimePattern()
    private var dateTime: Long = 0
    private fun setDateTime(selectedDateTime: Long) {
        dateTime = selectedDateTime
    }

    /*SPORT CENTER MANAGEMENT*/
    private val sportCenterIdLiveData = MutableLiveData(0)

    private fun setSportCenterId(id: Int) {
        sportCenterIdLiveData.value = id
    }

    /* COURT MANAGEMENT*/
    private var courtId: Int = 0
    private fun setCourtId(selectedCourtId: Int) {
        courtId = selectedCourtId
    }


    /* PRICE MANAGEMENT */
    private fun setTotalAmount(courtHourCharge: Float) {
        _totalAmountLiveData.value = courtHourCharge
    }

    private var _totalAmountLiveData = MutableLiveData(0f)

    val totalAmountLiveData: LiveData<Float> = _totalAmountLiveData


    /*SERVICES MANAGEMENT*/
    val services: LiveData<List<SportCenterServicesWithDetails>> =
        sportCenterIdLiveData.switchMap { sportCenterId ->
            sportCenterServicesRepository.sportCenterServicesWithDetails(sportCenterId)
        }


    fun getServicesWithFees(): List<ServiceWithFee> {
        return services.value?.map {
            ServiceWithFee(it.service, it.sportCenterServices.fee)
        } ?: listOf()
    }

    private fun getServiceFee(serviceId: Int): Float {
        return services.value?.find { it.sportCenterServices.idService == serviceId }?.sportCenterServices?.fee
            ?: 0f
    }


    private var selectedServices = MutableLiveData<MutableSet<Int>>().also {
        it.value = mutableSetOf()
    }

    fun addServiceIdToFilters(serviceId: Int) {
        val s = selectedServices.value
        s?.add(serviceId)
        selectedServices.value = s
        _totalAmountLiveData.value = _totalAmountLiveData.value?.plus(getServiceFee(serviceId))
    }

    fun removeServiceIdFromFilters(serviceId: Int) {
        val s = selectedServices.value
        s?.remove(serviceId)
        selectedServices.value = s
        _totalAmountLiveData.value = _totalAmountLiveData.value?.minus(getServiceFee(serviceId))

    }

    fun isServiceIdInList(serviceId: Int): Boolean {
        return selectedServices.value?.contains(serviceId) ?: false
    }

    /* COMPLETE RESERVATION*/
    @Transaction
    fun reserveCourt() {
        thread {
            val date = SearchSportCentersUtil.getDateTimeFormatted(
                dateTime,
                dateFormat
            )
            val time = SearchSportCentersUtil.getDateTimeFormatted(
                dateTime,
                timeFormat
            )

            val reservationId =
                reservationRepository.add(1, courtId, date, time, totalAmountLiveData.value!!)
            reservationServiceRepository.add(
                reservationId.toInt(),
                selectedServices.value!!.toList()
            )
        }
    }


}