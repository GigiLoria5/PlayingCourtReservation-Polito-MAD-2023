package it.polito.mad.g26.playingcourtreservation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.model.*
import it.polito.mad.g26.playingcourtreservation.repository.CourtRepository

class CourtVM(application: Application): AndroidViewModel(application) {
    val repo = CourtRepository(application)

    val courtList: LiveData<List<Court>> = repo.getAllCourt()

}