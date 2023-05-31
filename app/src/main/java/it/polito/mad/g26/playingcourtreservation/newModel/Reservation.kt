package it.polito.mad.g26.playingcourtreservation.newModel

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import it.polito.mad.g26.playingcourtreservation.util.getDigest
import java.text.SimpleDateFormat
import java.util.Locale

data class Reservation(
    var id: String = "",
    val userId: String = "",
    val sportCenterId: String = "",
    val courtId: String = "",
    var date: String = "",
    var time: String = "",
    val amount: Float = 0.0f,
    var services: List<String> = arrayListOf(),
    var participants: List<String> = arrayListOf(),
    var requests: List<String> = arrayListOf(),
    var invitees: List<String> = arrayListOf(),
    var reviews: List<Review> = arrayListOf()
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

        fun generateId(courtId: String, date: String, time: String): String =
            getDigest(courtId + date + time)
    }
}

data class Review(
    val userId: String = "",
    val rating: Float = 0.0F,
    val text: String? = null,
    @ServerTimestamp
    val date: Timestamp = Timestamp.now()
)

fun List<Review>.avg(): Float {
    val ratings = this.filter { !it.rating.isNaN() }.map { it.rating }
    return if (ratings.isEmpty()) 0.0f else ratings.average().toFloat()
}

fun timestampToDate(timestamp: Timestamp): String {
    val dateFormat = SimpleDateFormat("dd-MM-yyyy, HH:mm", Locale.getDefault())
    return dateFormat.format(timestamp.toDate())
}
