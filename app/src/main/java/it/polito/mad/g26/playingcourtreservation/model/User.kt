package it.polito.mad.g26.playingcourtreservation.model

import it.polito.mad.g26.playingcourtreservation.util.getDigest
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class User(
    val id: String = "",
    val username: String = "",
    val fullname: String = "Anonymous User",
    val birthDate: String? = null,
    val gender: String? = null,
    val location: String? = null,
    val position: String? = null,
    val skills: List<Skill> = emptyList()
) {
    companion object {
        const val BIRTHDATE_PATTERN = "dd-MM-yyyy"
        const val DEFAULT_BIRTHDATE = "N/A"
        const val DEFAULT_GENDER = "Unknown"
        const val DEFAULT_LOCATION = "Not specified"
        const val DEFAULT_POSITION = "Not specified"

        fun generateUsername(uid: String): String {
            val timestamp = System.currentTimeMillis()
            val input = "$uid-$timestamp"
            val hashString = getDigest(input)
            return "User_${hashString.substring(0, 10)}"
        }
    }

    fun getBirthDateOrDefault(): String = birthDate ?: DEFAULT_BIRTHDATE
    fun getGenderOrDefault(): String = gender ?: DEFAULT_GENDER
    fun getLocationOrDefault(): String = location ?: DEFAULT_LOCATION
    fun getPositionOrDefault(): String = position ?: DEFAULT_POSITION

    fun getAgeOrDefault(): String {
        if (birthDate == null)
            return DEFAULT_BIRTHDATE
        val birthDateObj = LocalDate.parse(
            birthDate,
            DateTimeFormatter.ofPattern(BIRTHDATE_PATTERN)
        )
        val currentDate = LocalDate.now()
        return ChronoUnit.YEARS.between(birthDateObj, currentDate).toString()
    }
}

data class Skill(
    val sportName: String = "",
    val rating: Float = 0.0f
)
