package it.polito.mad.g26.playingcourtreservation.repository

import android.app.Application
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.database.CourtReservationDatabase
import it.polito.mad.g26.playingcourtreservation.model.*

class CourtReservationRepository(application: Application) {
    private val courtReservationDao = CourtReservationDatabase.getDatabase(application).courtReservationDao()

     fun addSport(name: String){
         val i = Sport().also { it.name = name }
         courtReservationDao.addSport(i)
     }

    fun addSportCenter(name: String, address: String, city:String, longitude: Float,
                       latitude: Float, phoneNumber: String, open_time: String,
                       close_time: String){
        val i = SportCenter().also { it.name = name; it.address = address; it.city = city;
        it.longitude = longitude; it.latitude = latitude; it.phone_number = phoneNumber;
        it.open_time = open_time; it.close_time = close_time}
        courtReservationDao.addSportCenter(i)
    }

    fun addService(name: String){
        val i = Service().also { it.name = name}
        courtReservationDao.addService(i)
    }

    fun addSportCenterService(id_sport_center: Int, id_service: Int, fee: Float){
        val i = SportCenterServices().also { it.id_sport_center = id_sport_center;
            it.id_service = id_service; it.fee = fee}
        courtReservationDao.addSportCenterService(i)
    }

    fun addCourt(id_sport_center: Int, id_sport: Int, hour_charge: Float, name: String){
        val i = Court().also { it.id_sport_center = id_sport_center;
            it.id_sport = id_sport; it.hour_charge = hour_charge; it.name = name}
        courtReservationDao.addCourt(i)
    }

    fun addReservation(id_user: Int, id_court: Int, date: String, time: String, amount: Float){
        val i = Reservation().also { it.id_user = id_user;
            it.id_court = id_court; it.date = date; it.time = time; it.amount = amount}
        courtReservationDao.addReservation(i)
    }

    fun addReservationService(id_reservation: Int, id_service: Int){
        val i = ReservationServices().also { it.id_reservation = id_reservation; it.id_service = id_service}
        courtReservationDao.addReservationServices(i)
    }

    fun getAllSports(): LiveData<List<Sport>> = courtReservationDao.findAllSports()
    fun getAllSportCenters(): LiveData<List<SportCenter>> = courtReservationDao.findAllSportCenters()
    fun getAllServices(): LiveData<List<Service>> = courtReservationDao.findAllServices()
    fun getAllSportCenterServices(): LiveData<List<SportCenterServices>> = courtReservationDao.findAllSportCenterServices()
    fun getAllCourt(): LiveData<List<Court>> = courtReservationDao.findAllCourt()
    fun getAllReservation(): LiveData<List<Reservation>> = courtReservationDao.findAllReservation()
    fun getAllReservationServices(): LiveData<List<ReservationServices>> = courtReservationDao.findAllReservationServices()

    fun add(){
        val i = Service().also { it.name = "baech soccer" }
        courtReservationDao.addService(i)
    }

}