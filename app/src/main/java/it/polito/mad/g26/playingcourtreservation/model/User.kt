package it.polito.mad.g26.playingcourtreservation.model

import it.polito.mad.g26.playingcourtreservation.util.getDigest

data class User(
    val id: String = "",
    val username: String = "",
    val fullname: String = "Anonymous User",
    val age: Int? = null,
    val gender: String? = null,
    val location: String? = null,
    val position: String? = null,
    val skills: List<Skill> = emptyList()
) {
    companion object {
        fun generateUsername(uid: String): String {
            val timestamp = System.currentTimeMillis()
            val input = "$uid-$timestamp"
            val hashString = getDigest(input)
            return "User_${hashString.substring(0, 10)}"
        }
    }
}

data class Skill(
    val sportName: String = "",
    val rating: Float = 0.0f
)
