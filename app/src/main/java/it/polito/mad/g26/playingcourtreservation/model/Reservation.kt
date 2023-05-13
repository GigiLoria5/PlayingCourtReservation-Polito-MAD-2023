package it.polito.mad.g26.playingcourtreservation.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

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

    companion object {
        fun getReservationDatePattern(): String {
            return "dd-MM-yyyy"
        }
    }

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "id_user")
    var idUser: Int = 1

    @ColumnInfo(name = "id_court")
    var idCourt: Int = 0

    var date: String = ""

    var time: String = ""

    var amount: Float = 0.0F

    override fun toString() = "{ id: $id, id_user: $idUser, id_court: $idCourt, " +
            "date: \"$date\", time: \"$time\", amount: $amount }"

}
