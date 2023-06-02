package it.polito.mad.g26.playingcourtreservation.viewmodel

import androidx.lifecycle.ViewModel
import it.polito.mad.g26.playingcourtreservation.model.User

class SharedProfileViewModel : ViewModel() {
    var currentUserInfo = User()
}