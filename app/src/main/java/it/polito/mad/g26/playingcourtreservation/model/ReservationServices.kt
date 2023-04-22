package it.polito.mad.g26.playingcourtreservation.model

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "reservation_services", primaryKeys = ["id_reservation", "id_service"], foreignKeys = [
    ForeignKey(
        entity = Reservation::class,
        parentColumns = ["id"],
        childColumns = ["id_reservation"],
        onDelete = 5
    ),
    ForeignKey(
        entity = Service::class,
        parentColumns = ["id"],
        childColumns = ["id_service"],
        onDelete = 5
    )
])
class ReservationServices {
    var id_reservation:Int = 0

    var id_service:Int = 1

    override fun toString() = "{ id_reservation: $id_reservation, id_services: $id_service }"
}
