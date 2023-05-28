package it.polito.mad.g26.playingcourtreservation.newModel

data class User(
    val id: String = "",
    val username: String = "",
    val fullname: String = "Anonymous User",
    val age: Int? = null,
    val gender: String? = null,
    val location: String? = null,
    val position: String? = null,
    val skills: List<Pair<String, Double>> = emptyList()
)
