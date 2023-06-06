package it.polito.mad.g26.playingcourtreservation.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import it.polito.mad.g26.playingcourtreservation.util.getDigest

data class Reservation(
    var id: String = "",
    val userId: String = "",
    val sportCenterId: String = "",
    val courtId: String = "",
    var date: String = "",
    var time: String = "",
    var amount: Float = 0.0f,
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Reservation) return false

        if (id != other.id) return false
        if (userId != other.userId) return false
        if (sportCenterId != other.sportCenterId) return false
        if (courtId != other.courtId) return false
        if (date != other.date) return false
        if (time != other.time) return false
        if (amount != other.amount) return false
        if (services.sorted() != other.services.sorted()) return false
        if (participants.sorted() != other.participants.sorted()) return false
        if (requests.sorted() != other.requests.sorted()) return false
        if (invitees.sorted() != other.invitees.sorted()) return false
        if (reviews != other.reviews) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + userId.hashCode()
        result = 31 * result + sportCenterId.hashCode()
        result = 31 * result + courtId.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + time.hashCode()
        result = 31 * result + amount.hashCode()
        result = 31 * result + services.sorted().hashCode()
        result = 31 * result + participants.sorted().hashCode()
        result = 31 * result + requests.sorted().hashCode()
        result = 31 * result + invitees.sorted().hashCode()
        result = 31 * result + reviews.hashCode()
        return result
    }
}

data class Review(
    val userId: String = "",
    val rating: Float = 0.0F,
    val text: String? = null,
    @ServerTimestamp
    val timestamp: Timestamp = Timestamp.now()
)

fun List<Review>.avg(): Float {
    val ratings = this.filter { !it.rating.isNaN() }.map { it.rating }
    return if (ratings.isEmpty()) 0.0f else ratings.average().toFloat()
}
