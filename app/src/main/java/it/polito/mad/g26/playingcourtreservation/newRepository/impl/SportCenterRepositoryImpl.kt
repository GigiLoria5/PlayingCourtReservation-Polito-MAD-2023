package it.polito.mad.g26.playingcourtreservation.newRepository.impl

import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.g26.playingcourtreservation.newModel.SportCenter
import it.polito.mad.g26.playingcourtreservation.newRepository.SportCenterRepository

class SportCenterRepositoryImpl(val database: FirebaseFirestore) : SportCenterRepository {
    override fun getSportCenters(): List<SportCenter> {
        return arrayListOf()
    }

    override fun getSportCentersCities(): List<String> {
        return arrayListOf()
    }
}