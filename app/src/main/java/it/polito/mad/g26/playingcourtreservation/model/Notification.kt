package it.polito.mad.g26.playingcourtreservation.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

class Notification {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "id_user")
    var idUser: Int = 1

    @ColumnInfo(name = "id_reservation")
    var idReservation: Int = 0

    var timestamp: String = ""

    var message: String = ""

    var isRead: Boolean = false

    override fun toString() = "{ id_reservation: $idReservation, id_user: $idUser, timestamp: $timestamp, " +
            "message: \"$message\", isRead: $isRead }"
}