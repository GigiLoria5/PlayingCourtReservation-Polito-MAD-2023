package it.polito.mad.g26.playingcourtreservation.newModel

data class SportCenter(
    val id: String = "",
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
    val fee: Double = 0.0
)

data class Court(
    val id: String = "",
    val name: String = "",
    val sport: String = "",
    val hourCharge: Double = 0.0
)
