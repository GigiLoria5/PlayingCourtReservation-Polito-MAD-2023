package it.polito.mad.g26.playingcourtreservation.newRepository.impl

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.g26.playingcourtreservation.newModel.Court
import it.polito.mad.g26.playingcourtreservation.newModel.Service
import it.polito.mad.g26.playingcourtreservation.newModel.SportCenter
import it.polito.mad.g26.playingcourtreservation.newRepository.SportCenterRepository
import it.polito.mad.g26.playingcourtreservation.util.FirestoreCollections
import it.polito.mad.g26.playingcourtreservation.util.UiState
import it.polito.mad.g26.playingcourtreservation.util.await
import javax.inject.Inject

class SportCenterRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
) : SportCenterRepository {

    private val sportCenterNotFoundMsg = "Sport Center not found"

    override suspend fun getAllSportCenters(): UiState<List<SportCenter>> {
        return try {
            Log.d(TAG, "Performing getAllSportCenters")
            val result = db.collection(FirestoreCollections.SPORT_CENTERS)
                .orderBy("name")
                .get().await()
            Log.d(TAG, "getAllSportCenters: ${result.documents.size} results")
            val sportCenters = arrayListOf<SportCenter>()
            for (document in result) {
                val sportCenter = document.toObject(SportCenter::class.java)
                sportCenter.id = document.id
                sportCenters.add(sportCenter)
            }
            UiState.Success(sportCenters)
        } catch (e: Exception) {
            Log.e(TAG, "Error while performing getAllSportCenters: ${e.message}", e)
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun getSportCenterById(sportCenterId: String): UiState<SportCenter> {
        return try {
            Log.d(TAG, "Performing getSportCenterById with courtId: $sportCenterId")
            val result = db.collection(FirestoreCollections.SPORT_CENTERS)
                .document(sportCenterId)
                .get().await()
            Log.d(TAG, "getSportCenterById $sportCenterId: found=${result.exists()}")
            if (!result.exists()) // Court Id must be unique and existing
                UiState.Failure(sportCenterNotFoundMsg)
            val sportCenter = result.toObject(SportCenter::class.java)!!
            UiState.Success(sportCenter)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error while performing getSportCenterById $sportCenterId: ${e.message}",
                e
            )
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun getAllSportCentersCities(): UiState<List<String>> {
        return try {
            Log.d(TAG, "Performing getAllSportCentersCities")
            val result = db.collection(FirestoreCollections.SPORT_CENTERS)
                .orderBy("city")
                .get().await()
            Log.d(TAG, "getAllSportCentersCities: ${result.documents.size} results")
            val cities = result.mapNotNull { it.getString("city") }.distinct()
            UiState.Success(cities)
        } catch (e: Exception) {
            Log.e(TAG, "Error while performing getAllSportCentersCities: ${e.message}", e)
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun getFilteredSportCentersCities(cityNamePrefix: String): UiState<List<String>> {
        val capitalizedPrefix = cityNamePrefix.lowercase().replaceFirstChar(Char::titlecase)
        return try {
            Log.d(
                TAG,
                "Performing getFilteredSportCentersCities with cityNamePrefix: $capitalizedPrefix"
            )
            val result = db.collection(FirestoreCollections.SPORT_CENTERS)
                .orderBy("city")
                .startAt(capitalizedPrefix)
                .endAt("${capitalizedPrefix}\uf8ff")
                .get().await()
            Log.d(
                TAG,
                "getFilteredSportCentersCities $capitalizedPrefix: ${result.documents.size} results"
            )
            val cities = result.mapNotNull { it.getString("city") }.distinct()
            UiState.Success(cities)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error while performing getFilteredSportCentersCities $capitalizedPrefix: ${e.message}",
                e
            )
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun getAllSportCenterCourts(sportCenterId: String): UiState<List<Court>> {
        return try {
            Log.d(TAG, "Performing getAllSportCenterCourts with sportCenterId: $sportCenterId")
            val result = db.collection(FirestoreCollections.SPORT_CENTERS)
                .document(sportCenterId)
                .get().await()
            Log.d(TAG, "getAllSportCenterCourts $sportCenterId: found=${result.exists()}")
            if (!result.exists()) // Court Id must be unique and existing
                UiState.Failure(sportCenterNotFoundMsg)
            val courts = result.toObject(SportCenter::class.java)!!.courts.sortedBy { it.name }
            UiState.Success(courts)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error while performing getAllSportCenterCourts $sportCenterId: ${e.message}",
                e
            )
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun getAllSportCenterCourtsBySport(
        sportCenterId: String,
        sportName: String
    ): UiState<List<Court>> {
        return try {
            Log.d(
                TAG,
                "Performing getAllSportCenterCourtsBySport with sportCenterId: $sportCenterId and sportName: $sportName"
            )
            val result = db.collection(FirestoreCollections.SPORT_CENTERS)
                .document(sportCenterId)
                .get().await()
            Log.d(
                TAG,
                "getAllSportCenterCourtsBySport with sportCenterId: $sportCenterId and sportName: $sportName: found=${result.exists()}"
            )
            if (!result.exists()) // Court Id must be unique and existing
                UiState.Failure(sportCenterNotFoundMsg)
            val courts = result.toObject(SportCenter::class.java)!!.courts
                .filter { it.sport == sportName }
                .sortedBy { it.name }
            UiState.Success(courts)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error while performing getAllSportCenterCourtsBySport with sportCenterId: $sportCenterId and sportName: $sportName: ${e.message}",
                e
            )
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun getSportCenterServices(sportCenterId: String): UiState<List<Service>> {
        return try {
            Log.d(TAG, "Performing getSportCenterServices with sportCenterId: $sportCenterId")
            val result = db.collection(FirestoreCollections.SPORT_CENTERS)
                .document(sportCenterId)
                .get().await()
            Log.d(TAG, "getSportCenterServices $sportCenterId: found=${result.exists()}")
            if (!result.exists()) // Court Id must be unique and existing
                UiState.Failure(sportCenterNotFoundMsg)
            val services = result.toObject(SportCenter::class.java)!!.services.sortedBy { it.name }
            UiState.Success(services)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error while performing getSportCenterServices $sportCenterId: ${e.message}",
                e
            )
            UiState.Failure(e.localizedMessage)
        }
    }

    companion object {
        private const val TAG = "SportCenterRepository"
    }

}
