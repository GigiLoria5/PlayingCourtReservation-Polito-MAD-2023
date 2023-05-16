package it.polito.mad.g26.playingcourtreservation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.repository.SportCenterRepository

class SportCentersVM(application: Application) : AndroidViewModel(application) {

    private val repo = SportCenterRepository(application)

    fun getSportCenterName(sportCenterId: Int): LiveData<String> = repo.getSportCenterName(sportCenterId)

}
