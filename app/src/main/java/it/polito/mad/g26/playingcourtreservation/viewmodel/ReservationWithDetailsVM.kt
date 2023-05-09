package it.polito.mad.g26.playingcourtreservation.viewmodel

import android.app.Application
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Transaction
import it.polito.mad.g26.playingcourtreservation.model.ReservationWithDetails
import it.polito.mad.g26.playingcourtreservation.model.Service
import it.polito.mad.g26.playingcourtreservation.model.SportCenterServices
import it.polito.mad.g26.playingcourtreservation.model.custom.ServiceWithFee
import it.polito.mad.g26.playingcourtreservation.repository.ReservationServiceRepository
import it.polito.mad.g26.playingcourtreservation.repository.ReservationWithDetailsRepository
import it.polito.mad.g26.playingcourtreservation.util.ReservationWithDetailsUtil
import java.util.Locale
import kotlin.concurrent.thread

class ReservationWithDetailsVM(application: Application) : AndroidViewModel(application) {

    private val repo = ReservationWithDetailsRepository(application)
    private val repoSer = ReservationServiceRepository(application)

    val reservationWithDetails: LiveData<List<ReservationWithDetails>> =
        repo.reservationsWithDetails()

    fun getReservationWithDetailsById(id: Int): LiveData<ReservationWithDetails> =
        repo.reservationWithDetails(id)


    /*SERVICE MANAGEMENT*/

    //Take all services with fee from the database
    fun getAllServicesWithFee(id: Int): LiveData<List<SportCenterServices>> =
        repo.getAllServicesWithFee(id)

    //Get all services
    fun getAllServices(): LiveData<List<Service>> =
        repo.getAllServices()

    //Create a list of all the ServiceWithFee of the center
    fun allServiceWithoutSport(
        listFee: List<SportCenterServices>,
        listServ: List<Service>
    ): List<ServiceWithFee> {

        val servicesList = listFee.mapNotNull { sportCenterService ->
            val service = listServ.find { it.id == sportCenterService.idService }
            service?.let {
                ServiceWithFee(it, sportCenterService.fee)
            }
        }
        return servicesList
    }

    //Filter to chosen services
    fun filterServicesWithFee(
        servicesWithFee: List<ServiceWithFee>,
        services: List<Service>
    ): List<ServiceWithFee> {
        val serviceIds =
            services.map { it.id }.toSet() // create a set of service IDs for faster lookups
        return servicesWithFee.filter { it.service.id in serviceIds }
    }

    /*DELETE MANAGEMENT*/

    @Transaction
    fun deleteReservationById(id: Int) {
        thread {
            repo.deleteReservationById(id)
        }
    }

    /*UPDATE MANAGEMENT*/

    fun changeDateToFull(date: String): String {
        println(date)
        val sublist = date.split(" ")
        val month = listOf(
            "Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "Jun",
            "Jul",
            "Aug",
            "Sep",
            "Oct",
            "Nov",
            "Dec"
        )
        val allMonth = listOf(
            "-01",
            "-02",
            "-03",
            "-04",
            "-05",
            "-06",
            "-07",
            "-08",
            "-09",
            "-10",
            "-11",
            "-12"
        )
        val index = month.indexOfFirst { it == sublist[0] }
        val result = sublist[1] + allMonth[index] + "-2023"
        println(result)
        return result
    }

    fun getListOfIdService(list: List<ServiceWithFee>): List<Int> {
        return list.map { it.service.id }
    }

    @Transaction
    fun updateReservation(date: String, hour: String, id: Int, ids: List<Int>, amount: Float) {
        thread {
            // Perform the update operation
            repo.updateDateAndHourAndAmount(date, hour, id, amount)
            repo.deleteServices(id)
            repoSer.add(id, ids)
        }
    }

    fun findExistingReservation(date: String, hour: String): LiveData<Int?> {
        return repo.findDataAndHour(date, hour)
    }

    /*DATE TIME MANAGEMENT*/

    fun changeNumberToHour(number: Int): String {
        return "$number:00"
    }

    fun createDateFromInt(day: Int, month: Int, year: Int): String {
        return if (month < 10)
            "$day-0$month-$year"
        else
            "$day-$month-$year"
    }

    private val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    private val _selectedDateTimeMillis = MutableLiveData<Long>().also {
        it.value = ReservationWithDetailsUtil.getMockInitialDateTime().timeInMillis

    }
    val selectedDateTimeMillis: LiveData<Long> = _selectedDateTimeMillis
    fun changeSelectedDateTimeMillis(newTimeInMillis: Long) {
        _selectedDateTimeMillis.value = newTimeInMillis
    }

    fun takeIntCenterTime(centerTime: String): Int {
        val sublist = centerTime.split(":")
        return sublist[0].toInt()
    }

    fun createCalendarObject(date: String, time: String): Calendar {
        val calendar = Calendar.getInstance()
        calendar.time = dateFormatter.parse(date)
        val timeParts = time.split(":")
        calendar.set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
        calendar.set(Calendar.MINUTE, timeParts[1].toInt())
        return calendar
    }

}
