package it.polito.mad.g26.playingcourtreservation.model

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

    var longitude:Float? = null

    var latitude:Float? = null

    var phone_number:String = ""

    var open_time:String = ""

    var close_time:String = ""

    override fun toString() = "{ id: $id, name:\"$name\", address:\"$address\", " +
            "city:\"$city\", longitude:\"$longitude\", latitude:\"$latitude\", " +
            "phone_number:\"$phone_number\", open_time:\"$open_time\", close_time:\"$close_time\" }"
}