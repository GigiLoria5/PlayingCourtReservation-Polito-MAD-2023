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
        "5-a-side Football" -> R.color.sport_5_aside_football
        "8-a-side Football" -> R.color.sport_8_aside_football
        "11-a-side Football" -> R.color.sport_11_aside_football
        "Beach Soccer" -> R.color.sport_beach_soccer
        "Futsal" -> R.color.sport_futsal
        else -> R.color.custom_black
    }
}