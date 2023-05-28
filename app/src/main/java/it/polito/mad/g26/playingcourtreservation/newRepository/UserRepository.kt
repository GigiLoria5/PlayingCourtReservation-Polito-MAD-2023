package it.polito.mad.g26.playingcourtreservation.newRepository

import com.google.firebase.auth.FirebaseUser
import it.polito.mad.g26.playingcourtreservation.util.UiState

interface UserRepository {
    val currentUser: FirebaseUser?
    suspend fun login(): UiState<FirebaseUser>
}