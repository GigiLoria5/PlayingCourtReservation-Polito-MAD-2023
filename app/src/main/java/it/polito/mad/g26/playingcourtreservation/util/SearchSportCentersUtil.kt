package it.polito.mad.g26.playingcourtreservation.util

import android.app.DatePickerDialog
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.fragment.searchFragments.HomePageFragmentDirections
import it.polito.mad.g26.playingcourtreservation.fragment.searchFragments.SearchSportCentersFragmentDirections
import java.util.Locale

object SearchSportCentersUtil {

    /* OPERATIONS WITH CALENDAR OBJECTS */

    fun getMockInitialDateTime(): Long {
        val calendar = getDelayedCalendar()
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        return calendar.timeInMillis
    }

    private fun getDelayedCalendar(): Calendar {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR_OF_DAY, 1)
        calendar.add(Calendar.MINUTE, 30)
        return calendar
    }

    private fun adjustDateTimeCombination(mySelectionCalendar: Calendar) {
        val calendar = getDelayedCalendar()
        if (mySelectionCalendar.timeInMillis < calendar.timeInMillis) {
            mySelectionCalendar.apply {
                set(Calendar.YEAR, calendar[Calendar.YEAR])
                set(Calendar.MONTH, calendar[Calendar.MONTH])
                set(Calendar.DAY_OF_MONTH, calendar[Calendar.DAY_OF_MONTH])
                set(Calendar.HOUR_OF_DAY, calendar[Calendar.HOUR_OF_DAY])
                set(Calendar.MINUTE, 0)
            }
        }
    }

    /* SUPPORT FUNCTIONS */

    fun getDateTimeFormatted(
        timeInMillis: Long,
        format: String,
    ): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        var formatted = SimpleDateFormat(format, Locale.ENGLISH).format(calendar.time)
        if (formatted == "24:00") formatted = "00:00"
        return formatted
    }

    /* OPERATIONS WITH SearchSportCentersFragment VISUAL COMPONENTS */

    fun setDateTimeTextViews(
        timeInMillis: Long,
        dateFormat: String,
        hourFormat: String,
        dateTextView: TextView,
        hourTextView: TextView
    ) {
        dateTextView.text = getDateTimeFormatted(timeInMillis, dateFormat)
        hourTextView.text = getDateTimeFormatted(timeInMillis, hourFormat)
    }

    fun setAutoCompleteTextViewSport(
        viewContext: Context,
        sports: List<String>,
        courtTypeACTV: AutoCompleteTextView,
        selectedSport: String
    ) {
        val courtsType = mutableListOf(
            viewContext.getString(
                R.string.all_sports
            )
        )
        sports.sortedBy { it }.forEach { courtsType.add(it) }
        val adapterCourt = ArrayAdapter(viewContext, R.layout.list_item, courtsType)
        courtTypeACTV.setText(
            sports.find { it == selectedSport } ?: viewContext.getString(
                R.string.all_sports
            )
        )
        courtTypeACTV.setAdapter(adapterCourt)
        courtTypeACTV.dismissDropDown()
    }

    /* OPERATIONS WITH SearchSportCentersFragment DIALOG COMPONENTS*/

    fun showAndManageBehaviorDatePickerDialog(
        viewContext: Context,
        currentTimeMillis: Long,
        selectedTimeMillisTasks: (Long) -> Unit
    ) {
        val currentCalendar = Calendar.getInstance()
        currentCalendar.timeInMillis = currentTimeMillis


        showDatePickerDialog(
            viewContext,
            currentCalendar,
            minTimeMillis = getDelayedCalendar().timeInMillis,
        ) {
            adjustDateTimeCombination(it)
            selectedTimeMillisTasks(it.timeInMillis)
        }
    }

    fun showAndManageBehaviorTimePickerDialog(
        viewContext: Context,
        currentTimeMillis: Long,
        selectedTimeMillisTasks: (Long) -> Unit
    ) {
        val currentCalendar = Calendar.getInstance()
        currentCalendar.timeInMillis = currentTimeMillis
        val delayedCalendar = getDelayedCalendar()
        val minHour =
            if (currentCalendar.timeInMillis < delayedCalendar.timeInMillis || DateUtils.isToday(
                    currentCalendar.timeInMillis
                )
            ) {
                delayedCalendar[Calendar.HOUR_OF_DAY]
            } else
                0
        val currentHour = currentCalendar[Calendar.HOUR_OF_DAY]

        showTimePickerDialog(viewContext, minHour = minHour, currentHour = currentHour) {
            currentCalendar[Calendar.HOUR_OF_DAY] = it
            currentCalendar[Calendar.MINUTE] = 0
            //Once I update the date, I have to check the date-time pair to cancel possible errors
            adjustDateTimeCombination(currentCalendar)
            selectedTimeMillisTasks(currentCalendar.timeInMillis)
        }
    }

    /* OPERATIONS WITH SearchSportCentersFragment NAVIGATION  */

    fun navigateToAction(
        navController: NavController,
        city: String,
        dateTime: Long,
        sportName: String,
        selectedServicesNames: Array<String>
    ) {
        val direction =
            SearchSportCentersFragmentDirections.actionSearchSportCentersToSportCentersAction(
                city, "result", dateTime, sportName, selectedServicesNames
            )
        navController.navigate(direction)
    }

    fun navigateBack(navController: NavController, city: String, bornFrom: String) {
        if (bornFrom == "home")
            navController.popBackStack()
        else {
            navController.popBackStack()
            val direction =
                HomePageFragmentDirections.actionHomeToSportCentersAction(
                    "home", city, 0, "", arrayOf()
                )
            val options = NavOptions.Builder()
                .setEnterAnim(R.anim.from_left)
                .setExitAnim(R.anim.to_right)
                .setPopEnterAnim(R.anim.from_left)
                .setPopExitAnim(R.anim.to_right)
                .build()
            navController.navigate(direction, options)
        }
    }

    /* GUI CALENDAR AND TIME PICKER COMPONENTS */

    private fun showDatePickerDialog(
        viewContext: Context,
        calendar: Calendar,
        minTimeMillis: Long? = null,
        maxTimeMillis: Long? = null,
        selectedCalendarTasks: (Calendar) -> Unit
    ) {
        val datePickerAction = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendar.apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, day)
            }
            selectedCalendarTasks(calendar)
        }

        val datePickerDialog = DatePickerDialog(
            viewContext,
            datePickerAction,
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH],
        )
        if (minTimeMillis != null)
            datePickerDialog.datePicker.minDate = minTimeMillis
        if (maxTimeMillis != null)
            datePickerDialog.datePicker.maxDate = maxTimeMillis

        datePickerDialog.show()
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
