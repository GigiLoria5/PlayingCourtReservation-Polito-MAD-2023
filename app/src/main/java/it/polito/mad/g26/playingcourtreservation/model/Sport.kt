package it.polito.mad.g26.playingcourtreservation.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import it.polito.mad.g26.playingcourtreservation.R

@Entity(tableName = "sport", indices = [Index("name")])
class Sport {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    var name: String = ""

    override fun toString() = "{ id: $id, name:\"$name\" }"
}

// Remember to update it when adding new sports
fun Sport.toSportColor(): Int {
    return when (name) {
        "5-a-side football" -> R.color.sport5asidefootball
        "8-a-side football" -> R.color.sport8asidefootball
        "11-a-side football" -> R.color.sport11asidefootball
        "Beach soccer" -> R.color.sportbeachsoccer
        "Futsal" -> R.color.sportfutsal
        else -> R.color.custom_black
    }
}