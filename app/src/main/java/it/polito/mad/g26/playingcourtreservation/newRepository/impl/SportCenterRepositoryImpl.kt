package it.polito.mad.g26.playingcourtreservation.newRepository.impl

import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.g26.playingcourtreservation.newModel.SportCenter
import it.polito.mad.g26.playingcourtreservation.newRepository.SportCenterRepository
import it.polito.mad.g26.playingcourtreservation.util.FirestoreTables
import it.polito.mad.g26.playingcourtreservation.util.UiState
import it.polito.mad.g26.playingcourtreservation.util.await

class SportCenterRepositoryImpl(private val db: FirebaseFirestore) : SportCenterRepository {

    override suspend fun addSportCenters(sportCenters: List<SportCenter>): UiState<String> {
        return try {
            val batch = db.batch()
            sportCenters.forEach { sportCenter ->
                val docRef = db.collection(FirestoreTables.SPORT_CENTERS).document()
                sportCenter.id = docRef.id
                batch[docRef] = sportCenter
            }
            batch.commit().await()
            UiState.Success("Sport centers added successfully")
        } catch (e: Exception) {
            e.printStackTrace()
            UiState.Failure(e.localizedMessage)
        }
    }

    override suspend fun getSportCenters(): UiState<List<SportCenter>> {
        return try {
            val result = db.collection(FirestoreTables.SPORT_CENTERS).get().await()
            val sportCenters = arrayListOf<SportCenter>()
            for (document in result) {
                val sportCenter = document.toObject(SportCenter::class.java)
                sportCenter.id = document.id
                sportCenters.add(sportCenter)
            }
            sportCenters.sortBy { list -> list.name }
            UiState.Success(sportCenters)
        } catch (e: Exception) {
            e.printStackTrace()
            UiState.Failure(e.message)
        }
    }

    override suspend fun getSportCentersCities(): UiState<List<String>> {
        return try {
            val result = db.collection(FirestoreTables.SPORT_CENTERS).get().await()
            val cities = result.mapNotNull { it.getString("city") }.distinct()
            UiState.Success(cities)
        } catch (e: Exception) {
            e.printStackTrace()
            UiState.Failure(e.message)
        }
    }

}
