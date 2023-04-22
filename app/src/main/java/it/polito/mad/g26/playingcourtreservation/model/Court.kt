package it.polito.mad.g26.playingcourtreservation.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "court", foreignKeys = [
    ForeignKey(
        entity = SportCenter::class,
        parentColumns = ["id"],
        childColumns = ["id_sport_center"],
        onDelete = 5
    ),
    ForeignKey(
        entity = Sport::class,
        parentColumns = ["id"],
        childColumns = ["id_sport"],
        onDelete = 5
    )
])
class Court {
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0

    var id_sport_center:Int = 0

    var id_sport:Int = 0

    var hour_charge:Float = 0.0F

    var name:String = ""

    override fun toString() = "{ id: $id, id_sport_center: $id_sport_center, id_sport: $id_sport, " +
            "hour_charge:$hour_charge, name:\"$name\" }"
}