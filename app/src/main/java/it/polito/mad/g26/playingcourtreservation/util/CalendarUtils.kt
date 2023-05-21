package it.polito.mad.g26.playingcourtreservation.util

import com.kizitonwose.calendar.core.Week
import com.kizitonwose.calendar.core.yearMonth
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale

fun YearMonth.displayText(short: Boolean = false): String {
    return "${this.month.displayText(short = short)} ${this.year}"
}

fun Month.displayText(short: Boolean = true): String {
    val style = if (short) TextStyle.SHORT else TextStyle.FULL
    return getDisplayName(style, Locale.getDefault())
}

fun DayOfWeek.displayText(uppercase: Boolean = false): String {
    return getDisplayName(TextStyle.SHORT, Locale.getDefault()).let { value ->
        if (uppercase) value.uppercase(Locale.getDefault()) else value
    }
}

fun LocalDate.displayDay(): String {
    val dateFormatter = DateTimeFormatter.ofPattern("dd")
    return dateFormatter.format(this)
}

fun getWeekPageTitle(week: Week): String {
    val firstDate = week.days.first().date
    val lastDate = week.days.last().date
    return getWeekPageTitle(firstDate, lastDate)
}

fun getWeekPageTitle(localDate: LocalDate): String {
    val weekFields = WeekFields.of(Locale.getDefault())
    val firstDayOfWeek = weekFields.firstDayOfWeek
    val startOfWeek = localDate.with(firstDayOfWeek)
        .let { if (it.dayOfWeek == firstDayOfWeek) it else it.with(firstDayOfWeek) }
        .let { if (it > localDate) it.minusDays(7) else it }
    val endOfWeek = startOfWeek.plusDays(6)
    return getWeekPageTitle(startOfWeek, endOfWeek)
}

fun getWeekPageTitle(startOfWeek: LocalDate, endOfWeek: LocalDate): String {
    return when {
        startOfWeek.yearMonth == endOfWeek.yearMonth -> {
            startOfWeek.yearMonth.displayText()
        }

        startOfWeek.year == endOfWeek.year -> {
            "${startOfWeek.month.displayText(short = false)} - ${endOfWeek.yearMonth.displayText()}"
        }

        else -> {
            "${startOfWeek.yearMonth.displayText()} - ${endOfWeek.yearMonth.displayText()}"
        }
    }
}

