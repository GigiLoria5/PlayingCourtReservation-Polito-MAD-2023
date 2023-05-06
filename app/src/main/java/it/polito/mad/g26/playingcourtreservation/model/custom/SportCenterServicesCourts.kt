package it.polito.mad.g26.playingcourtreservation.model.custom

import androidx.room.Embedded
import androidx.room.Relation
import it.polito.mad.g26.playingcourtreservation.model.Court
import it.polito.mad.g26.playingcourtreservation.model.SportCenter
import it.polito.mad.g26.playingcourtreservation.model.SportCenterServices

data class SportCenterServicesCourts(
    @Embedded val sportCenter: SportCenter,

    @Relation(
        parentColumn = "id", // sportCenter
        entityColumn = "id_sport_center", // sportCenterServices
    )
    var sportCenterServices: List<SportCenterServices>,

    @Relation(
        parentColumn = "id", // sportCenter
        entityColumn = "id_sport_center" //court
    )
    var courts: List<Court>
)
