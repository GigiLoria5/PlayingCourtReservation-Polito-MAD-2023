package it.polito.mad.g26.playingcourtreservation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.room.Transaction
import it.polito.mad.g26.playingcourtreservation.model.ReservationWithDetails
import it.polito.mad.g26.playingcourtreservation.model.Service
import it.polito.mad.g26.playingcourtreservation.model.SportCenterServices
import it.polito.mad.g26.playingcourtreservation.model.custom.ServiceWithFee
import it.polito.mad.g26.playingcourtreservation.repository.ReservationWithDetailsRepository
import kotlin.concurrent.thread

class ReservationWithDetailsVM(application: Application) : AndroidViewModel(application) {

    private val repo = ReservationWithDetailsRepository(application)

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


    //return only services with feed that are used
    fun servicesUsedFee(lAll: List<ServiceWithFee>, lUsed: List<Service>): List<ServiceWithFee> {
        val result = lAll.filter { serviceWithFee ->
            lUsed.any { usedService ->
                usedService.id == serviceWithFee.service.id
            }
        }
        return result
    }

    /*DELETE MANAGEMENT*/

    @Transaction
    fun deleteReservationById(id: Int) {
        thread {
            repo.deleteReservationById(id)
        }
    }

    /*UPDATE MANAGEMENT*/
    /*
        fun changeDate(date:String):String{
            val sublist=date.split(" ")
            val month=listOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")
            val day=listOf(1,2,3,4,5,6,7,8,9)

            if(sublist[0])
        }

        @Transaction
        fun updateReservation(date:String, hour:String): Boolean {

            var isUpdateSuccessful = false
            val result = repo.findDataAndHour(date, hour)

            result.observeForever { reservationId ->
                if (reservationId == null) {
                    thread {
                        // Perform the update operation
                        repo.updateDateAndHour(date, hour, 1)
                        isUpdateSuccessful = true
                    }
                } else {
                    isUpdateSuccessful = false
                }
            }

            return isUpdateSuccessful
        }
    */
}
