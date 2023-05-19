package it.polito.mad.g26.playingcourtreservation.model.custom

import it.polito.mad.g26.playingcourtreservation.model.CourtWithDetails
import it.polito.mad.g26.playingcourtreservation.model.SportCenter

data class SportCenterWithMoreDetailsFormatted(
    val sportCenter: SportCenter,
    val servicesWithFee: List<ServiceWithFee>,
    val sportCenterReviewsSummary: SportCenterReviewsSummary,
    val courts: List<CourtWithDetails>
)