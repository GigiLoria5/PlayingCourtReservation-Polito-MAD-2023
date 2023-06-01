package it.polito.mad.g26.playingcourtreservation.util

import java.security.MessageDigest

fun getDigest(string: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    val hashBytes = md.digest(string.toByteArray())
    return hashBytes.joinToString("") { "%02x".format(it) }
}