package it.polito.mad.g26.playingcourtreservation.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.ForeignKey.Companion.CASCADE

@Entity(tableName = "court", foreignKeys = [
    ForeignKey(
        entity = SportCenter::class,
        parentColumns = ["id"],
        childColumns = ["id_sport_center"],
        onDelete = CASCADE
    ),
    ForeignKey(
        entity = Sport::class,
        parentColumns = ["id"],
        childColumns = ["id_sport"],
        onDelete = CASCADE
    )
])
class Court {
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0

    @ColumnInfo(name = "id_sport_center")
    var idSportCenter:Int = 0

    @ColumnInfo(name = "id_sport")
    var idSport:Int = 0

    @ColumnInfo(name = "hour_charge")
    var hourCharge:Float = 0.0F

    var name:String = ""

    override fun toString() = "{ id: $id, id_sport_center: $idSportCenter, id_sport: $idSport, " +
            "hour_charge:$hourCharge, name:\"$name\" }"
}