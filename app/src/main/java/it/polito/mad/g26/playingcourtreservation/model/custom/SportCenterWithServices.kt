package it.polito.mad.g26.playingcourtreservation.model.custom

import androidx.room.Embedded
import androidx.room.Relation
import it.polito.mad.g26.playingcourtreservation.model.SportCenter
import it.polito.mad.g26.playingcourtreservation.model.SportCenterServices

data class SportCenterWithServices(
    @Embedded val sportCenter: SportCenter,

    @Relation(
        entity = SportCenterServices::class,
        parentColumn = "id", // sportCenter
        entityColumn = "id_sport_center", // sportCenterServices
    )
    var sportCenterServicesWithDetails: List<SportCenterServicesWithDetails>
)
