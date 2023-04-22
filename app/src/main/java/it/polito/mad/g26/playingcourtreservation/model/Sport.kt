package it.polito.mad.g26.playingcourtreservation.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "sport", indices = [Index("name")])
class Sport {
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0

    var name:String = ""

    override fun toString() = "{ id: $id, name:\"$name\" }"
}