package it.polito.mad.g26.playingcourtreservation.repository.impl

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import it.polito.mad.g26.playingcourtreservation.model.User
import it.polito.mad.g26.playingcourtreservation.repository.UserRepository
import it.polito.mad.g26.playingcourtreservation.util.FirebaseStorageConstants
import it.polito.mad.g26.playingcourtreservation.util.FirebaseStorageConstants.MAX_DOWNLOAD_SIZE
import it.polito.mad.g26.playingcourtreservation.util.FirestoreCollections
import it.polito.mad.g26.playingcourtreservation.util.UiState
import it.polito.mad.g26.playingcourtreservation.util.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: StorageReference
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
            val username = User.generateUsername(uid)
            val user = User(id = uid, username = username, usernameLowercase = username.lowercase())
            db.collection(FirestoreCollections.USERS)
                .document(uid)
                .set(user).await()
            Log.d(TAG, "User document added to Firestore collection with ID $uid")
            UiState.Success(result.user!!)
        } catch (e: Exception) {
            Log.e(TAG, "Error during anonymous authentication operation: ${e.message}", e)
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun getCurrentUserInformation(): UiState<User> {
        val userId = currentUser!!.uid
        return try {
            Log.d(TAG, "Performing getCurrentUserInformation for user with id $userId")
            val document = db.collection(FirestoreCollections.USERS)
                .document(userId)
                .get().await()
            Log.d(
                TAG,
                "getCurrentUserInformation for user with id $userId found? ${document.exists()}"
            )
            val user = document.toObject(User::class.java)!!
            UiState.Success(user)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error performing getCurrentUserInformation for user with id $userId: ${e.message}",
                e
            )
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun getUserInformationById(userId: String): UiState<User> {
        return try {
            Log.d(TAG, "Performing getUserInformationById for user with id $userId")
            val document = db.collection(FirestoreCollections.USERS)
                .document(userId)
                .get().await()
            Log.d(
                TAG,
                "getUserInformationById for user with id $userId found? ${document.exists()}"
            )
            val user = document.toObject(User::class.java)!!
            UiState.Success(user)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error performing getUserInformationById for user with id $userId: ${e.message}",
                e
            )
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun getUserInformationByUsername(username: String): UiState<User?> {
        return try {
            Log.d(TAG, "Performing getUserInformationByUsername for user with username $username")
            val result = db.collection(FirestoreCollections.USERS)
                .whereEqualTo("usernameLowercase", username.lowercase())
                .get().await()
            Log.d(
                TAG,
                "getUserInformationByUsername for user with username $username found? ${result.documents.isNotEmpty()}"
            )
            var user: User? = null
            if (result.documents.isNotEmpty())
                user = result.documents.first().toObject(User::class.java)
            Log.d(TAG, "The user found with username $username is $user")
            UiState.Success(user)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error performing getUserInformationByUsername for user with username $username: ${e.message}",
                e
            )
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun getAllUsers(): UiState<List<User>> {
        return try {
            Log.d(TAG, "getFilteredUsers")
            val result = db.collection(FirestoreCollections.USERS)
                .get().await()
            Log.d(TAG, "getFilteredUsers result")
            val users = arrayListOf<User>()
            for (document in result) {
                val user = document.toObject(User::class.java)
                users.add(user)
            }
            UiState.Success(users)
        } catch (e: Exception) {
            Log.e(TAG, "Error while performing getFilteredUsers", e)
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun updateCurrentUserInformation(updatedUserInformation: User): UiState<Unit> {
        return try {
            val userId = currentUser!!.uid
            updatedUserInformation.usernameLowercase = updatedUserInformation.username.lowercase()
            Log.d(TAG, "Performing updateCurrentUserInformation for user with id $userId")
            Log.d(TAG, "$updatedUserInformation")
            if (userId != updatedUserInformation.id)
                return UiState.Failure("Impossible to update user information")
            db.collection(FirestoreCollections.USERS)
                .document(userId)
                .set(updatedUserInformation).await()
            Log.d(TAG, "User document with ID $userId updated in Firestore collection")
            UiState.Success(Unit)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error while performing updateCurrentUserInformation for user ${updatedUserInformation.id}: ${e.message}",
                e
            )
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun updateUserImage(imageData: ByteArray): UiState<Unit> {
        val userId = currentUser!!.uid
        return try {
            Log.d(TAG, "Performing updateUserImage for user with id $userId")
            storage
                .child(getStorageFileName(userId))
                .putBytes(imageData)
                .await()
            Log.d(TAG, "User image with ID $userId updated in Firebase storage")
            UiState.Success(Unit)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error while performing updateUserImage for user with id ${userId}: ${e.message}",
                e
            )
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun downloadUserImage(userId: String): UiState<ByteArray?> {
        return try {
            Log.d(TAG, "Performing downloadUserImage for user with id $userId")
            val imageData = storage
                .child(getStorageFileName(userId))
                .getBytes(MAX_DOWNLOAD_SIZE)
                .await()
            Log.d(TAG, "User image with ID $userId downloaded from Firebase storage")
            UiState.Success(imageData)
        } catch (e: StorageException) {
            Log.d(TAG, "User image with ID $userId not found in Firebase storage")
            UiState.Success(null)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error while performing downloadUserImage for user with id ${userId}: ${e.message}",
                e
            )
            UiState.Failure(e.localizedMessage)
        }
    }

    private fun getStorageFileName(userId: String): String {
        return "${FirebaseStorageConstants.USER_IMAGES}/$userId.jpg"
    }

    companion object {
        private const val TAG = "UserRepository"
    }

}
