package it.polito.mad.g26.playingcourtreservation.newRepository.impl

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.g26.playingcourtreservation.newModel.User
import it.polito.mad.g26.playingcourtreservation.newRepository.UserRepository
import it.polito.mad.g26.playingcourtreservation.util.FirestoreCollections
import it.polito.mad.g26.playingcourtreservation.util.UiState
import it.polito.mad.g26.playingcourtreservation.util.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : UserRepository {

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override suspend fun login(): UiState<FirebaseUser> {
        return try {
            // Authenticate the user anonymously with Firebase Auth
            val result = auth.signInAnonymously().await()
            Log.d(TAG, "signInAnonymously: success")
            // Add user information to Firestore's collection
            val uid = result.user!!.uid
            val user = User(id = uid, username = User.generateUsername(uid))
            db.collection(FirestoreCollections.USERS)
                .document(uid)
                .set(user)
                .await()
            Log.d(TAG, "User document added to Firestore collection with ID $uid")
            UiState.Success(result.user!!)
        } catch (e: Exception) {
            Log.e(TAG, "Error during anonymous authentication operation: ${e.message}", e)
            UiState.Failure(e.localizedMessage)
        }
    }

    companion object {
        private const val TAG = "UserRepository"
    }

}
