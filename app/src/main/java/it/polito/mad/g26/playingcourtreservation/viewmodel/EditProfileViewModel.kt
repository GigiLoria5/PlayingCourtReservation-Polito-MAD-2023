package it.polito.mad.g26.playingcourtreservation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.g26.playingcourtreservation.model.User
import it.polito.mad.g26.playingcourtreservation.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private var _userInformation = User()
    val userInformation: User
        get() = _userInformation

    fun initialize(user: User) {
        _userInformation = user
    }

}
