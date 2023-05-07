package it.polito.mad.g26.playingcourtreservation.model

import androidx.room.Embedded
import androidx.room.Relation

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
