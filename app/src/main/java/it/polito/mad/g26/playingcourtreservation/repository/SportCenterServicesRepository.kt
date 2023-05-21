package it.polito.mad.g26.playingcourtreservation.repository

import android.app.Application
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.database.CourtReservationDatabase
import it.polito.mad.g26.playingcourtreservation.model.custom.SportCenterServicesWithDetails

class SportCenterServicesRepository(application: Application) {
    private val sportCenterServicesDao =
        CourtReservationDatabase.getDatabase(application).sportCenterServiceDao()

    fun sportCenterServicesWithDetails(sportCenterId: Int): LiveData<List<SportCenterServicesWithDetails>> =
        sportCenterServicesDao.getAllServicesWithFeeDetailed(sportCenterId)


}