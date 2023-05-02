package it.polito.mad.g26.playingcourtreservation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.model.*
import it.polito.mad.g26.playingcourtreservation.repository.SportCenterRepository

class SportCenterVM(application: Application): AndroidViewModel(application) {
    val repo = SportCenterRepository(application)

    val sportCenterList: LiveData<List<SportCenter>> = repo.findAllSportCenters()
}