package it.polito.mad.g26.playingcourtreservation.model.custom

data class CourtReviewsSummary(
    var courtId:Int,
    val avg: Double,
    val count: Long,
)