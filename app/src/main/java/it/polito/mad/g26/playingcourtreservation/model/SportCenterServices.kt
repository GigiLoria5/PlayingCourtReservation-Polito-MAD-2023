package it.polito.mad.g26.playingcourtreservation.model

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "sport_center_services", primaryKeys = ["id_sport_center", "id_service"], foreignKeys = [
    ForeignKey(
        entity = SportCenter::class,
        parentColumns = ["id"],
        childColumns = ["id_sport_center"],
        onDelete = 5
    ),
    ForeignKey(
        entity = Service::class,
        parentColumns = ["id"],
        childColumns = ["id_service"],
        onDelete = 5
    )
])
class SportCenterServices {
    var id_sport_center:Int = 0

    var id_service:Int = 0

    var fee:Float = 0.0F

    override fun toString() = "{ id_sport_center: $id_sport_center, id_services: $id_service, fee: $fee }"
}