package it.polito.mad.g26.playingcourtreservation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.model.*
import it.polito.mad.g26.playingcourtreservation.repository.ServiceRepository

class ServiceVM(application: Application): AndroidViewModel(application) {
    private val repo = ServiceRepository(application)

    val serviceList: LiveData<List<Service>> = repo.services()
}