package it.polito.mad.g26.playingcourtreservation.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.ForeignKey.Companion.CASCADE

@Entity(
    tableName = "reservation", foreignKeys = [
        ForeignKey(
            entity = Court::class,
            parentColumns = ["id"],
            childColumns = ["id_court"],
            onDelete = CASCADE
        )
    ]
)
class Reservation {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "id_user")
    var idUser: Int = 1

    @ColumnInfo(name = "id_court")
    var idCourt: Int = 0

    // Pattern: dd-MM-yyyy
    var date: String = ""

    var time: String = ""

    var amount: Float = 0.0F

    override fun toString() = "{ id: $id, id_user: $idUser, id_court: $idCourt, " +
            "date: \"$date\", time: \"$time\", amount: $amount }"
}