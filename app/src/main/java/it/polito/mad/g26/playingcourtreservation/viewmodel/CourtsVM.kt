package it.polito.mad.g26.playingcourtreservation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.model.Court
import it.polito.mad.g26.playingcourtreservation.repository.CourtRepository

class CourtsVM(application: Application) : AndroidViewModel(application) {

    private val repo = CourtRepository(application)

    fun getCourt(courtId: Int): LiveData<Court> = repo.getCourt(courtId)

}
