package it.polito.mad.g26.playingcourtreservation.newRepository.impl

import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.g26.playingcourtreservation.newModel.SportCenter
import it.polito.mad.g26.playingcourtreservation.newRepository.SportCenterRepository
import it.polito.mad.g26.playingcourtreservation.util.UiState

class SportCenterRepositoryImpl(val database: FirebaseFirestore) : SportCenterRepository {
    override fun getSportCenters(): UiState<List<SportCenter>> {
        // Get data from Firebase
        val data = arrayListOf<SportCenter>()
        return if (data.isNullOrEmpty())
            UiState.Failure("Data is empty")
        else
            UiState.Success(data)
    }

    override fun getSportCentersCities(): UiState<List<String>> {
        // Get data from Firebase
        val data = arrayListOf<String>()
        return if (data.isNullOrEmpty())
            UiState.Failure("Data is empty")
        else
            UiState.Success(data)
    }
}