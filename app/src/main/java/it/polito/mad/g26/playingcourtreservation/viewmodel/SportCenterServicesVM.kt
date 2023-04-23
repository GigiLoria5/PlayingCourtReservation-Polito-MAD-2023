package it.polito.mad.g26.playingcourtreservation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.model.*
import it.polito.mad.g26.playingcourtreservation.repository.SportCenterServicesRepository

class SportCenterServicesVM(application: Application): AndroidViewModel(application) {
    val repo = SportCenterServicesRepository(application)

    val sportCenterServicesList: LiveData<List<SportCenterServices>> = repo.getAllSportCenterServices()

}