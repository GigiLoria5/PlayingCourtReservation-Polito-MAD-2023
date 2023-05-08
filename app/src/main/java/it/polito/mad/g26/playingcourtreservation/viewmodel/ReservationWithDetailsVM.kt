package it.polito.mad.g26.playingcourtreservation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.room.Transaction
import it.polito.mad.g26.playingcourtreservation.model.ReservationWithDetails
import it.polito.mad.g26.playingcourtreservation.model.Service
import it.polito.mad.g26.playingcourtreservation.model.SportCenterServices
import it.polito.mad.g26.playingcourtreservation.model.custom.ServiceWithFee
import it.polito.mad.g26.playingcourtreservation.repository.ReservationServiceRepository
import it.polito.mad.g26.playingcourtreservation.repository.ReservationWithDetailsRepository
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

class ReservationWithDetailsVM(application: Application) : AndroidViewModel(application) {

    private val repo = ReservationWithDetailsRepository(application)
    private val repoSer= ReservationServiceRepository(application)

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

    fun changeDateToSplit(date:String):String{
        val sublist=date.split("-")
        println("vediamo come lo divide")
        println(sublist)

        val month=listOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")
        val allMonth=listOf("01","02","03","04","05","06","07","08","09","10","11","12")
        val index = allMonth.indexOfFirst { it == sublist[1] }
        val result=month[index] + " " + sublist[0]
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

        fun changeDateToFull(date:String):String{
            val sublist=date.split(" ")

            val month=listOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")
            val allMonth=listOf("-01","-02","-03","-04","-05","-06","-07","-08","-09","-10","-11","-12")
            val index = month.indexOfFirst { it == sublist[0] }
            val result=sublist[1]+allMonth[index]+"-2023"
            return result

        }

        fun getListOfIdService(list: List<ServiceWithFee>): List<Int>{
            return list.map{it.service.id}
        }

        @Transaction
        fun updateReservation(date:String, hour:String,id :Int,ids:List<Int>): Boolean {

            var isUpdateSuccessful = false

            val latch = CountDownLatch(1)
                    thread {
                        // Perform the update operation
                        repo.updateDateAndHour(date, hour, id)
                        repo.deleteServices(id)
                        repoSer.add(id,ids)

                        isUpdateSuccessful = true
                        latch.countDown()
                    }
            latch.await()
            return isUpdateSuccessful
        }

    fun findExisting(date:String, hour:String):LiveData<Int?>{
        return repo.findDataAndHour(date, hour)
    }

}
