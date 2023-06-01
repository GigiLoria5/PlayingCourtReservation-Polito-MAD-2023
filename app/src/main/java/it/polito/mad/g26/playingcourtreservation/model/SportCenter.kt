package it.polito.mad.g26.playingcourtreservation.model

import it.polito.mad.g26.playingcourtreservation.R
import java.util.UUID

data class SportCenter(
    var id: String = "",
    val name: String = "",
    val address: String = "",
    val city: String = "",
    val phoneNumber: String = "",
    val openTime: String = "",
    val closeTime: String = "",
    val services: List<Service> = emptyList(),
    val courts: List<Court> = emptyList()
)

data class Service(
    val name: String = "",
    val fee: Float = 0.0F
)

data class Court(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val sport: String = "",
    val hourCharge: Float = 0.0F
) {
    companion object {
        fun getSportColor(sportName: String): Int {
            return when (sportName) {
                "5-a-side Football" -> R.color.sport_5_aside_football
                "8-a-side Football" -> R.color.sport_8_aside_football
                "11-a-side Football" -> R.color.sport_11_aside_football
                "Beach Soccer" -> R.color.sport_beach_soccer
                "Futsal" -> R.color.sport_futsal
                else -> R.color.custom_black
            }
        }

        fun getSportTotParticipants(sportName: String): Int {
            return when (sportName) {
                "5-a-side Football" -> 10
                "8-a-side Football" -> 16
                "11-a-side Football" -> 22
                "Beach Soccer" -> 10
                "Futsal" -> 10
                else -> 0
            }
        }

    }
}
