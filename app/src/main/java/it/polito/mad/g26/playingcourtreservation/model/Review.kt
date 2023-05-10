package it.polito.mad.g26.playingcourtreservation.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
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
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "id_reservation")
    var idReservation: Int = 0

    var rating: Float = 0.0F

    @ColumnInfo(name = "text_review")
    var textReview: String = ""

    override fun toString() = "{ id: $id, id_reservation: $idReservation, rating: $rating, " +
            "text_review: \"$textReview\"}"
}