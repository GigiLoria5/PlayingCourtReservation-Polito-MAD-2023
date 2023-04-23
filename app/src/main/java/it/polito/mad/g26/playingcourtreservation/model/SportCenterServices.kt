package it.polito.mad.g26.playingcourtreservation.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE


@Entity(tableName = "sport_center_services", primaryKeys = ["id_sport_center", "id_service"], foreignKeys = [
    ForeignKey(
        entity = SportCenter::class,
        parentColumns = ["id"],
        childColumns = ["id_sport_center"],
        onDelete = CASCADE
    ),
    ForeignKey(
        entity = Service::class,
        parentColumns = ["id"],
        childColumns = ["id_service"],
        onDelete = CASCADE
    )
])
class SportCenterServices {
    @ColumnInfo(name = "id_sport_center")
    var idSportCenter:Int = 0

    @ColumnInfo(name = "id_service")
    var idService:Int = 0

    var fee:Float = 0.0F

    override fun toString() = "{ id_sport_center: $idSportCenter, id_services: $idService, fee: $fee }"
}