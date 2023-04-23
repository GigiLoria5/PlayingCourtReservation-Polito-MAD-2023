package it.polito.mad.g26.playingcourtreservation.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE

@Entity(tableName = "reservation_services", primaryKeys = ["id_reservation", "id_service"], foreignKeys = [
    ForeignKey(
        entity = Reservation::class,
        parentColumns = ["id"],
        childColumns = ["id_reservation"],
        onDelete = CASCADE
    ),
    ForeignKey(
        entity = Service::class,
        parentColumns = ["id"],
        childColumns = ["id_service"],
        onDelete = CASCADE
    )
])
class ReservationServices {
    @ColumnInfo(name = "id_reservation")
    var idReservation:Int = 0

    @ColumnInfo(name = "id_service")
    var idService:Int = 0

    override fun toString() = "{ id_reservation: $idReservation, id_services: $idService }"
}
