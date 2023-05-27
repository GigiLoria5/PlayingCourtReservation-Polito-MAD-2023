package it.polito.mad.g26.playingcourtreservation.newModel

data class SportCenter(
    val id: String,
    val name: String,
    val address: String,
    val city: String,
    val phoneNumber: String,
    val openTime: String,
    val closeTime: String,
    val services: List<Service>,
    val courts: List<Court>
)

data class Service(
    val name: String,
    val fee: Double
)

data class Court(
    val id: String,
    val name: String,
    val sport: String,
    val hourCharge: Double
)
