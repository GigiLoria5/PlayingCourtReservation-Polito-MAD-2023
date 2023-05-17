package it.polito.mad.g26.playingcourtreservation.model.custom

import it.polito.mad.g26.playingcourtreservation.model.SportCenter

data class SportCenterWithServicesFormatted(
    val sportCenter: SportCenter,
    val servicesWithFee: List<ServiceWithFee>,
)