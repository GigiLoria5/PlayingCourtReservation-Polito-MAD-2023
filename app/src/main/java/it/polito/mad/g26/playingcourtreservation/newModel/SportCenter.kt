package it.polito.mad.g26.playingcourtreservation.newModel

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
)
