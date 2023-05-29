package it.polito.mad.g26.playingcourtreservation.newModel

import com.google.firebase.firestore.ServerTimestamp

data class Reservation(
    var id: String = "",
    val idUser: String = "",
    val idCourt: String = "",
    val date: String = "",
    val time: String = "",
    val amount: Double = 0.0,
    val services: List<String> = listOf(),
    val participants: List<String> = listOf(),
    val requests: List<String> = listOf(),
    val invitees: List<String> = listOf(),
    val reviews: List<Review> = listOf()
) {
    companion object {
        private const val DATE_PATTERN = "dd-MM-yyyy"
        private const val TIME_PATTERN = "HH:mm"

        fun getDatePattern(): String {
            return DATE_PATTERN
        }

        fun getTimePattern(): String {
            return TIME_PATTERN
        }
    }
}

data class Review(
    val idUser: String = "",
    val rating: Double = 0.0,
    val text: String? = null,
    @ServerTimestamp
    val date: String = ""
)

fun List<Review>.avg(): Double = this.map { it.rating }.average()
