package it.polito.mad.g26.playingcourtreservation.newRepository.impl

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.g26.playingcourtreservation.newModel.SportCenter
import it.polito.mad.g26.playingcourtreservation.newRepository.SportCenterRepository
import it.polito.mad.g26.playingcourtreservation.util.FirestoreCollections
import it.polito.mad.g26.playingcourtreservation.util.UiState
import it.polito.mad.g26.playingcourtreservation.util.await
import javax.inject.Inject

class SportCenterRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
) : SportCenterRepository {

    override suspend fun getSportCenters(): UiState<List<SportCenter>> {
        return try {
            val result = db.collection(FirestoreCollections.SPORT_CENTERS)
                .orderBy("name")
                .get().await()
            val sportCenters = arrayListOf<SportCenter>()
            for (document in result) {
                val sportCenter = document.toObject(SportCenter::class.java)
                sportCenter.id = document.id
                sportCenters.add(sportCenter)
            }
            sportCenters.sortBy { list -> list.name }
            UiState.Success(sportCenters)
        } catch (e: Exception) {
            Log.e(TAG, "Error while performing getSportCenters: ${e.message}", e)
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun getAllSportCentersCities(): UiState<List<String>> {
        return try {
            val result = db.collection(FirestoreCollections.SPORT_CENTERS)
                .orderBy("city")
                .get().await()
            val cities = result.mapNotNull { it.getString("city") }.distinct()
            UiState.Success(cities)
        } catch (e: Exception) {
            Log.e(TAG, "Error while performing getAllSportCentersCities: ${e.message}", e)
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun getFilteredSportCentersCities(cityNamePrefix: String): UiState<List<String>> {
        return try {
            val capitalizedPrefix = cityNamePrefix.lowercase().replaceFirstChar(Char::titlecase)
            val result = db.collection(FirestoreCollections.SPORT_CENTERS)
                .orderBy("city")
                .startAt(capitalizedPrefix)
                .endAt("${capitalizedPrefix}\uf8ff")
                .get().await()
            val cities = result.mapNotNull { it.getString("city") }.distinct()
            UiState.Success(cities)
        } catch (e: Exception) {
            Log.e(TAG, "Error while performing getSportCentersCities: ${e.message}", e)
            UiState.Failure(e.localizedMessage)
        }
    }

    companion object {
        private const val TAG = "SportCenterRepository"
    }

}
