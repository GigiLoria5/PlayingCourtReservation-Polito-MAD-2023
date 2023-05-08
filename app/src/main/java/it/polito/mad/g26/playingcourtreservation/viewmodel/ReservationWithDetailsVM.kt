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
    fun allServiceWithoutSport(listFee: List<SportCenterServices>, listServ: List<Service> ): List<ServiceWithFee>{

        val servicesList = listFee.mapNotNull { sportCenterService ->
            val service = listServ.find { it.id == sportCenterService.idService }
            service?.let {
                ServiceWithFee(it, sportCenterService.fee)
            }
        }
        return servicesList

    }

        /*val services=repo.getAllServicesWithFee(id).value
        println("print retunr of the database")
        println(services)
        var servicesList=services?.mapNotNull { sportCenterService ->
            //I need the second calling because there isn't the name inside SportCenterServices
            val service = repo.getServiceById(sportCenterService.idService).value
            println("print retunr of the database of services")
            println(service)
            if(service!=null)
                ServiceWithFee(service, sportCenterService.fee)
            else
                null
        }?: emptyList()
        return servicesList*/








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
