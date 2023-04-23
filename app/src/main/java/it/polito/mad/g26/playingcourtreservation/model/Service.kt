package it.polito.mad.g26.playingcourtreservation.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "service")
class Service {
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0

    var name:String = ""

    override fun toString() = "{ id: $id, name:\"$name\" }"
}