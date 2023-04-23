package it.polito.mad.g26.playingcourtreservation.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "sport_center", indices = [Index("name")])
class SportCenter {
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0

    var name:String = ""

    var address:String = ""

    var city:String = ""

    var longitude:Double? = null

    var latitude:Double? = null

    @ColumnInfo(name = "phone_number")
    var phoneNumber:String = ""

    @ColumnInfo(name = "open_time")
    var openTime:String = ""

    @ColumnInfo(name = "close_time")
    var closeTime:String = ""

    override fun toString() = "{ id: $id, name:\"$name\", address:\"$address\", " +
            "city:\"$city\", longitude:\"$longitude\", latitude:\"$latitude\", " +
            "phone_number:\"$phoneNumber\", open_time:\"$openTime\", close_time:\"$closeTime\" }"
}