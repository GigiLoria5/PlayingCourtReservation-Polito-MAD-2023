package it.polito.mad.g26.playingcourtreservation.model.custom

import androidx.room.Embedded
import androidx.room.Relation
import it.polito.mad.g26.playingcourtreservation.model.Court
import it.polito.mad.g26.playingcourtreservation.model.Sport
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
        entity = Court::class,
        parentColumn = "id", // sportCenter
        entityColumn = "id_sport_center" //court
    )
    var courtsWithDetails: List<CourtWithDetails>
)

data class CourtWithDetails(
    @Embedded val court: Court,
    @Relation(
        parentColumn = "id_sport_center",
        entityColumn = "id"
    )
    val sportCenter: SportCenter,
    @Relation(
        parentColumn = "id_sport",
        entityColumn = "id"
    )
    val sport: Sport
)