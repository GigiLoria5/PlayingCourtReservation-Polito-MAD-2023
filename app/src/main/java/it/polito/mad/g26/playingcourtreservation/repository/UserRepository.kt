package it.polito.mad.g26.playingcourtreservation.repository

import com.google.firebase.auth.FirebaseUser
import it.polito.mad.g26.playingcourtreservation.model.User
import it.polito.mad.g26.playingcourtreservation.util.UiState

interface UserRepository {
    val currentUser: FirebaseUser?

    suspend fun login(): UiState<FirebaseUser>

    suspend fun getCurrentUserInformation(): UiState<User>

    suspend fun getUserInformationById(userId: String): UiState<User>
}