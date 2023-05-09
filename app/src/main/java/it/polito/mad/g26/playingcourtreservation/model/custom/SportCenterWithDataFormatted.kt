package it.polito.mad.g26.playingcourtreservation.model.custom
import it.polito.mad.g26.playingcourtreservation.model.SportCenter

data class SportCenterWithDataFormatted(
    val sportCenter: SportCenter,
    val courtsWithDetails: List<CourtWithDetails>,
    val servicesWithFee: List<ServiceWithFee>,
)