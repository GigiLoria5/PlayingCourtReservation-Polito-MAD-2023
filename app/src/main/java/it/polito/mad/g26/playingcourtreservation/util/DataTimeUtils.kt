package it.polito.mad.g26.playingcourtreservation.util


import it.polito.mad.g26.playingcourtreservation.model.Reservation.Companion.getReservationDatePattern
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun changeNumberToHour(number: Int): String {
    return "$number:00"
}

fun createDateFromInt(day: Int, month: Int, year: Int): String {
    val date = LocalDate.of(year, month, day)
    val formatter = DateTimeFormatter.ofPattern(getReservationDatePattern(), Locale.getDefault())
    return date.format(formatter)
}

fun takeIntCenterTime(centerTime: String): Int {
    val sublist = centerTime.split(":")
    return sublist[0].toInt()
}

fun changeDateToFull(date: String): String {
    val formatter = DateTimeFormatter.ofPattern("MMM d yyyy")
    val dateObj = LocalDate.parse("$date ${LocalDate.now().year}", formatter)
    return dateObj.format(DateTimeFormatter.ofPattern(getReservationDatePattern()))
}

fun createCalendarObject(date: String, time: String): Calendar {
    val dateFormatter = SimpleDateFormat(getReservationDatePattern(), Locale.getDefault())
    val calendar = Calendar.getInstance()
    calendar.time = dateFormatter.parse(date) as Date
    val timeParts = time.split(":")
    calendar[Calendar.HOUR_OF_DAY] = timeParts[0].toInt()
    calendar[Calendar.MINUTE] = timeParts[1].toInt()
    return calendar
}