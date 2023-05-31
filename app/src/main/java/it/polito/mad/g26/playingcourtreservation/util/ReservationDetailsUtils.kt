package it.polito.mad.g26.playingcourtreservation.util

import android.content.Context
import android.icu.util.Calendar
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.newModel.SportCenter
import it.polito.mad.g26.playingcourtreservation.util.SearchSportCentersUtils.adjustDateTimeCombination
import java.time.LocalTime
import kotlin.math.max

object ReservationDetailsUtils {

    fun getMockInitialDateTime(): Calendar {
        val c = getDelayedCalendar()
        c[Calendar.MINUTE] = 0
        return c
    }

    private fun getDelayedCalendar(): Calendar {
        val c = Calendar.getInstance()
        c.add(Calendar.HOUR_OF_DAY, 1)
        c.add(Calendar.MINUTE, 30)
        return c
    }

    fun showAndManageBehaviorTimePickerDialog(
        viewContext: Context,
        currentTimeMillis: Long,
        sportCenter: SportCenter,
        selectedTimeMillisTasks: (Long) -> Unit
    ) {
        val currentCalendar = Calendar.getInstance()
        currentCalendar.timeInMillis = currentTimeMillis
        val delayedCalendar = getDelayedCalendar()
        val centerMinHour = LocalTime.parse(sportCenter.openTime).hour
        val centerMaxHour = LocalTime.parse(sportCenter.closeTime).hour
        val minHour = if (DateUtils.isToday(currentCalendar.timeInMillis)) {
            max(delayedCalendar[Calendar.HOUR_OF_DAY], centerMinHour)
        } else {
            centerMinHour
        }
        val maxHour = centerMaxHour - 1
        val currentHour = currentCalendar[Calendar.HOUR_OF_DAY]

        showTimePickerDialog(
            viewContext,
            minHour = minHour,
            maxHour = maxHour,
            currentHour = currentHour
        ) {
            currentCalendar[Calendar.HOUR_OF_DAY] = it
            currentCalendar[Calendar.MINUTE] = 0
            //Once I update the date, I have to check the date-time pair to cancel possible errors
            adjustDateTimeCombination(currentCalendar)
            selectedTimeMillisTasks(currentCalendar.timeInMillis)
        }
    }

    private fun showTimePickerDialog(
        viewContext: Context,
        minHour: Int = 0,
        maxHour: Int = 23,
        currentHour: Int = 0,
        selectedHourTasks: (Int) -> Unit
    ) {
        val linearLayout = LayoutInflater.from(viewContext)
            .inflate(R.layout.search_sport_centers_hour_picker, null)
        val numberPicker = linearLayout.findViewById<NumberPicker>(R.id.hourPicker)
        numberPicker.wrapSelectorWheel = false

        numberPicker.minValue = minHour
        numberPicker.maxValue = maxHour
        numberPicker.value = currentHour

        numberPicker.displayedValues = (0..23).toList().map { if (it < 10) "0$it:00" else "$it:00" }
            .slice(numberPicker.minValue..23).toTypedArray()

        val builder = AlertDialog.Builder(viewContext)
        builder.setTitle(
            viewContext.getString(
                R.string.select_hour
            )
        )
        builder.setMessage(
            viewContext.getString(
                R.string.select_hour_description
            )
        )
        builder.setView(linearLayout)
        builder.setPositiveButton("OK") { _, _ ->
            selectedHourTasks(numberPicker.value)
        }
        builder.setNegativeButton("Cancel") { _, _ -> }
        val dialog = builder.create()
        dialog.show()
    }

}