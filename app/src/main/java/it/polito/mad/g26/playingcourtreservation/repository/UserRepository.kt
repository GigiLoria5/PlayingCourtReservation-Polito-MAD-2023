package it.polito.mad.g26.playingcourtreservation.repository

import com.google.firebase.auth.FirebaseUser
import it.polito.mad.g26.playingcourtreservation.model.User
import it.polito.mad.g26.playingcourtreservation.util.UiState

interface UserRepository {
    val currentUser: FirebaseUser?

    suspend fun login(): UiState<FirebaseUser>

    suspend fun getCurrentUserInformation(): UiState<User>

    suspend fun getUserInformationById(userId: String): UiState<User>

    suspend fun getUserInformationByUsername(username: String): UiState<User?>

    suspend fun getAllUsers(): UiState<List<User>>

    suspend fun updateCurrentUserInformation(updatedUserInformation: User): UiState<Unit>

    suspend fun updateUserImage(imageData: ByteArray): UiState<Unit>

    suspend fun downloadUserImage(userId: String): UiState<ByteArray?>
}