package it.polito.mad.g26.playingcourtreservation.util

import java.security.MessageDigest

fun generateUsername(uid: String): String {
    val timestamp = System.currentTimeMillis()
    val input = "$uid-$timestamp"
    val md = MessageDigest.getInstance("SHA-256")
    val hashBytes = md.digest(input.toByteArray())
    val hashString = hashBytes.joinToString("") { "%02x".format(it) }
    return "User_${hashString.substring(0, 10)}"
}