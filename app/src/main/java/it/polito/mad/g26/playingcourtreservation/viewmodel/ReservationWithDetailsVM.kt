package it.polito.mad.g26.playingcourtreservation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.model.ReservationWithDetails
import it.polito.mad.g26.playingcourtreservation.model.Service
import it.polito.mad.g26.playingcourtreservation.model.SportCenterServices
import it.polito.mad.g26.playingcourtreservation.model.custom.ServiceWithFee
import it.polito.mad.g26.playingcourtreservation.repository.ReservationWithDetailsRepository

class ReservationWithDetailsVM(application: Application) : AndroidViewModel(application) {

    private val repo = ReservationWithDetailsRepository(application)

    val reservationWithDetails: LiveData<List<ReservationWithDetails>> =
        repo.reservationsWithDetails()

    fun getReservationWithDetailsById(id: Int): LiveData<ReservationWithDetails> =
        repo.reservationWithDetails(id)

    //Take all services with fee from the database
    fun getAllServicesWithFee(id:Int): LiveData<List<SportCenterServices>> =
        repo.getAllServicesWithFee(id)

    fun getAllServices() : LiveData<List<Service>> =
        repo.getAllServices()

    //Create a list of all the ServiceWithFee of the center
    fun allServiceWithoutSport(listFee: List<SportCenterServices>, listServ: List<Service> ): List<ServiceWithFee>{

        val servicesList = listFee.mapNotNull { sportCenterService ->
            val service = listServ.find { it.id == sportCenterService.idService }
            service?.let {
                ServiceWithFee(it, sportCenterService.fee)
            }
        }
        return servicesList

    }

    //Filter to chosen services
    fun filterServicesWithFee(servicesWithFee: List<ServiceWithFee>, services: List<Service>): List<ServiceWithFee> {
        val serviceIds = services.map { it.id }.toSet() // create a set of service IDs for faster lookups
        return servicesWithFee.filter { it.service.id in serviceIds }
    }





    //return only services with feed that are used
    fun servicesUsedFee(lAll : List<ServiceWithFee>,lUsed: List<Service> ): List<ServiceWithFee>{
        val result=lAll.filter { serviceWithFee ->
            lUsed.any { usedService ->
                usedService.id == serviceWithFee.service.id
            }
        }
        return result
    }

}
