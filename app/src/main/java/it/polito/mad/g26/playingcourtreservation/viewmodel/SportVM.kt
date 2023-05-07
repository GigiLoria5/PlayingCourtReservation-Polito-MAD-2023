package it.polito.mad.g26.playingcourtreservation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.model.*
import it.polito.mad.g26.playingcourtreservation.repository.SportRepository

class SportVM(application: Application): AndroidViewModel(application) {
    private val repo = SportRepository(application)

    val sportsList: LiveData<List<Sport>> = repo.sports()

}