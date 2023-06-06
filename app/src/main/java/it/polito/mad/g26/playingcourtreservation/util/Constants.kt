package it.polito.mad.g26.playingcourtreservation.util

object FirestoreCollections {
    const val USERS = "users"
    const val SPORT_CENTERS = "sportCenters"
    const val RESERVATIONS = "reservations"
    const val NOTIFICATIONS = "notifications"
}

object SportNames {
    const val FIVE_A_SIDE_FOOTBALL = "5-a-side Football"
    const val EIGHT_A_SIDE_FOOTBALL = "8-a-side Football"
    const val ELEVEN_A_SIDE_FOOTBALL = "11-a-side Football"
    const val BEACH_SOCCER = "Beach Soccer"
    const val FUTSAL = "Futsal"
}

object FirebaseStorageConstants {
    const val USER_IMAGES = "user"
    const val MAX_DOWNLOAD_SIZE = (10 * 1024 * 1024).toLong() // 10 MB
}