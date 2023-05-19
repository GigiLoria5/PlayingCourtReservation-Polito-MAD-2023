package it.polito.mad.g26.playingcourtreservation.model.custom

import androidx.room.Embedded
import androidx.room.Relation
import it.polito.mad.g26.playingcourtreservation.model.Service
import it.polito.mad.g26.playingcourtreservation.model.SportCenterServices

data class SportCenterServicesWithDetails(
    @Embedded val sportCenterServices: SportCenterServices,
    @Relation(
        parentColumn = "id_service",
        entityColumn = "id"
    )
    val service: Service
)

