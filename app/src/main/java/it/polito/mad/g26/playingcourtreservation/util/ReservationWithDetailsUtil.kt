package it.polito.mad.g26.playingcourtreservation.util

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.widget.TextView
import java.util.Locale

object ReservationWithDetailsUtil {

    fun getMockInitialDateTime(): Calendar {
        val c = getDelayedCalendar()
        c[Calendar.MINUTE] = 0
        return c
    }

    fun setDateTimeTextViews(
        timeInMillis: Long,
        dateFormat: String,
        hourFormat: String,
        dateTextView: TextView,
        hourTextView: TextView,
        newHourTextView: TextView
    ) {

        dateTextView.text = getDateTimeFormatted(timeInMillis, dateFormat)
        hourTextView.text = getDateTimeFormatted(timeInMillis, hourFormat)
        newHourTextView.text = getDateTimeFormatted(timeInMillis, hourFormat)
    }

    private fun getDateTimeFormatted(
        timeInMillis: Long,
        format: String,
    ): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        var formatted = SimpleDateFormat(format, Locale.ENGLISH).format(calendar.time)
        if (formatted == "24:00") formatted = "00:00"
        return formatted
    }

    private fun getDelayedCalendar(): Calendar {
        val c = Calendar.getInstance()
        c.add(Calendar.HOUR_OF_DAY, 1)
        c.add(Calendar.MINUTE, 30)
        return c
    }

    private fun adjustDateDateCombination(mySelectionCalendar: Calendar) {
        val c = getDelayedCalendar()
        if (mySelectionCalendar.timeInMillis < c.timeInMillis) {
            mySelectionCalendar.apply {
                set(Calendar.YEAR, c[Calendar.YEAR])
                set(Calendar.MONTH, c[Calendar.MONTH])
                set(Calendar.DAY_OF_MONTH, c[Calendar.DAY_OF_MONTH])
                set(Calendar.HOUR_OF_DAY, c[Calendar.HOUR_OF_DAY])
                set(Calendar.MINUTE, 0)
            }
        }
    }

}