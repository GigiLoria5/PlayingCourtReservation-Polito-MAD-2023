package it.polito.mad.g26.playingcourtreservation.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "reservation", foreignKeys = [
    ForeignKey(
        entity = Court::class,
        parentColumns = ["id"],
        childColumns = ["id_court"],
        onDelete = 5
    )
])
class Reservation {
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0

    var id_user:Int = 1

    var id_court:Int = 0

    var date:String = ""

    var time:String = ""

    var amount:Float = 0.0F

    override fun toString() = "{ id: $id, id_user: $id_user, id_court: $id_court, " +
            "date: \"$date\", time: \"$time\", amount: $amount }"
}