package it.polito.mad.g26.playingcourtreservation.model.custom
import it.polito.mad.g26.playingcourtreservation.model.Service

data class ServiceWithFee(
    val service: Service,
    val fee: Float,
)