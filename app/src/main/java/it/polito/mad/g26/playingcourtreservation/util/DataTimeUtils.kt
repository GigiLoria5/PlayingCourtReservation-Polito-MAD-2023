package it.polito.mad.g26.playingcourtreservation.util


import com.google.firebase.Timestamp
import it.polito.mad.g26.playingcourtreservation.model.Reservation
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun createCalendarObject(date: String, time: String): Calendar {
    val dateFormatter = SimpleDateFormat(Reservation.getDatePattern(), Locale.getDefault())
    val calendar = Calendar.getInstance()
    calendar.time = dateFormatter.parse(date) as Date
    val timeParts = time.split(":")
    calendar[Calendar.HOUR_OF_DAY] = timeParts[0].toInt()
    calendar[Calendar.MINUTE] = timeParts[1].toInt()
    return calendar
}

fun timestampToDate(timestamp: Timestamp): String {
    val dateFormat = SimpleDateFormat(
        "${Reservation.getDatePattern()}, ${Reservation.getTimePattern()}",
        Locale.getDefault()
    )
    return dateFormat.format(timestamp.toDate())
}