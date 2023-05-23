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
import it.polito.mad.g26.playingcourtreservation.fragment.searchFragments.SearchSportCentersFragmentDirections
import it.polito.mad.g26.playingcourtreservation.fragment.searchFragments.SearchSportCentersHomeFragmentDirections
import it.polito.mad.g26.playingcourtreservation.model.Sport
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
        sports: List<Sport>?,
        courtTypeACTV: AutoCompleteTextView,
        selectedSport: Int
    ) {
        val courtsType = mutableListOf(
            viewContext.getString(
                R.string.all_sports
            )
        )
        sports?.sortedBy { it.name }?.forEach { courtsType.add(it.name) }
        val adapterCourt =
            ArrayAdapter(viewContext, R.layout.list_item, courtsType)
        courtTypeACTV.setText(
            sports?.find { it.id == selectedSport }?.name ?: viewContext.getString(
                R.string.all_sports
            )
        )
        courtTypeACTV.setAdapter(adapterCourt)
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
        sportId: Int,
        selectedServicesIds: IntArray
    ) {
        val direction =
            SearchSportCentersFragmentDirections.actionSearchSportCentersToSportCentersAction(
                city, "result", dateTime, sportId, selectedServicesIds
            )
        navController.navigate(direction)
    }

    fun navigateBack(navController: NavController, city: String, bornFrom: String) {
        if (bornFrom == "home")
            navController.popBackStack()
        else {
            navController.popBackStack()
            val direction =
                SearchSportCentersHomeFragmentDirections.actionHomeToSportCentersAction(
                    "home", city, 0, 0, intArrayOf()
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
        val linearLayout = LayoutInflater.from(viewContext).inflate(R.layout.search_sport_centers_hour_picker, null)
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

    /* RESERVATION CONFIRM */
    /*
 fun showConfirmReservationDialog(
     viewContext: Context,
     courtWithDetails: CourtWithDetails,
     vm: SearchCourtResultsVM
 ) {
     val timeInMillis = vm.selectedDateTimeMillis.value ?: 0
     var total: Float = courtWithDetails.court.hourCharge
     var selectedServicesString = viewContext.getString(
         R.string.selected_services
     )
     val selectedServicesIds: MutableList<Int> = mutableListOf()
     vm.getSelectedServicesAndFees(courtWithDetails.sportCenter.id).forEach {
         total += it.fee
         selectedServicesIds.add(it.service.id)
         selectedServicesString += viewContext.getString(
             R.string.confirm_reservation_message_service_field,
             it.service.name,
             String.format("%.2f", it.fee)
         )
     }
     if (selectedServicesIds.isEmpty())
         selectedServicesString = viewContext.getString(
             R.string.no_selected_services
         )
     val reservationConfirmationText = viewContext.getString(
         R.string.confirm_reservation_message,
         courtWithDetails.court.name,
         courtWithDetails.sport.name,
         courtWithDetails.sportCenter.name,
         courtWithDetails.sportCenter.city,
         getDateTimeFormatted(timeInMillis, viewContext.getString(R.string.hourFormat)),
         getDateTimeFormatted(timeInMillis, viewContext.getString(R.string.dateExtendedFormat)),
         selectedServicesString,
         String.format("%.2f", courtWithDetails.court.hourCharge),
         String.format("%.2f", total),
     )


     val builder: AlertDialog.Builder =
         AlertDialog.Builder(viewContext)
             .setTitle(
                 viewContext.getString(
                     R.string.reserve_this_court
                 )
             )
             .setMessage(reservationConfirmationText)
             .setPositiveButton(
                 viewContext.getString(
                     R.string.reserve_button
                 )
             ) { _, _ ->
                 vm.reserveCourt(
                     courtWithDetails.court.id,
                     total,
                     selectedServicesIds
                 )
             }.setNegativeButton(
                 viewContext.getString(
                     R.string.cancel_button
                 )
             ) { _, _ -> }
             .setOnCancelListener { }
     val dialog = builder.create()
     dialog.show()
 }
*/

}