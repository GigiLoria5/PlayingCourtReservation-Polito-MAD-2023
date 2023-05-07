package it.polito.mad.g26.playingcourtreservation.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ReservationWithDetails(
    @Embedded val reservation: Reservation,
    @Relation(
        entity = Court::class,
        parentColumn = "id_court",
        entityColumn = "id"
    )
    val courtWithDetails: CourtWithDetails,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            ReservationServices::class,
            parentColumn = "id_reservation",
            entityColumn = "id_service"
        )
    )
    val services: List<Service>
)
