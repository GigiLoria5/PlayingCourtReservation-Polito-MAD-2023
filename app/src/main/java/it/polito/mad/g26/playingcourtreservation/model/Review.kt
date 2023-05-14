package it.polito.mad.g26.playingcourtreservation.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    primaryKeys = ["id_reservation", "id_user"],
    tableName = "review", foreignKeys = [
        ForeignKey(
            entity = Reservation::class,
            parentColumns = ["id"],
            childColumns = ["id_reservation"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class Review {
    @ColumnInfo(name = "id_reservation")
    var idReservation: Int = 0

    @ColumnInfo(name = "id_user")
    var idUser: Int = 1

    var rating: Float = 0.0F

    var text: String = ""

    var date: String = ""

    override fun toString() = "{ id_reservation: $idReservation, id_user: $idUser, rating: $rating, " +
            "text: \"$text\"}"
}