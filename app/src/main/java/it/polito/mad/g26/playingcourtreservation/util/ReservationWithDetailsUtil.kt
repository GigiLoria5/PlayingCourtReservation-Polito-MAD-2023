package it.polito.mad.g26.playingcourtreservation.util

import android.app.DatePickerDialog
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LifecycleOwner
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.viewmodel.ReservationWithDetailsVM
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale
import kotlin.math.max

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

    fun showDatePickerDialog(
        viewContext: Context, vm: ReservationWithDetailsVM, ownReservationId: Int,
        timeChosen: TextView, life: LifecycleOwner, dateNew: TextView, centerCloseTime:Int
    ) {

        val c = Calendar.getInstance()
        c.timeInMillis = vm.selectedDateTimeMillis.value!!

        //Alert dialog design
        val builderFound = AlertDialog.Builder(viewContext, R.style.MyAlertDialogStyle)
        builderFound.setMessage("There is already a reservation for this date and time")
        builderFound.setPositiveButton("Back") { _, _ ->
            // User clicked Back button
        }


        val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, day ->

            //+1 because start from zero to count months
            val date = createDateFromInt(day, month + 1, year)
            val hour = timeChosen.text.toString()
            val id = vm.findExistingReservation(date, hour)
            id.observe(life) { idReturned ->
                if (idReturned == null || idReturned == ownReservationId) {
                    dateNew.text = date
                    //Set and adjust if time is outside
                    c.apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month)
                        set(Calendar.DAY_OF_MONTH, day)
                    }
                    adjustDateDateCombination(c)
                    vm.changeSelectedDateTimeMillis(c.timeInMillis)
                } else {
                    builderFound.show()
                }
            }

        }

        val datePickerDialog = DatePickerDialog(
            viewContext,
            datePicker,
            c[Calendar.YEAR],
            c[Calendar.MONTH],
            c[Calendar.DAY_OF_MONTH],
        )

        val minDate = getDelayedCalendar()
        if(minDate[Calendar.HOUR_OF_DAY]>=centerCloseTime)
        //updating the minimum day if sportCenter is already closed
        {
            val localDate = LocalDate.now()

            // Add one day to the LocalDate
            val updatedLocalDate = localDate.plusDays(1)

            // Convert LocalDate back to Calendar
            val updatedCalendar = Calendar.getInstance()
            updatedCalendar.time = java.util.Date.from(updatedLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
            datePickerDialog.datePicker.minDate = updatedCalendar.timeInMillis
        }else
            datePickerDialog.datePicker.minDate = minDate.timeInMillis

        datePickerDialog.show()
    }

    fun showNumberPickerDialog(
        viewContext: Context, vm: ReservationWithDetailsVM, centerMinHour: Int, centerMaxHour: Int,
        ownReservationId: Int, dateChosen: TextView, life: LifecycleOwner, timeNew: TextView
    ) {
        val c = Calendar.getInstance()
        c.timeInMillis = vm.selectedDateTimeMillis.value!!

        // Alert dialog design
        val builderFound = AlertDialog.Builder(viewContext, R.style.MyAlertDialogStyle)
        builderFound.setMessage("There is already a reservation for this date and time")
        builderFound.setPositiveButton("Back") { _, _ ->
            // User clicked Back button
        }

        val linearLayout = LayoutInflater.from(viewContext).inflate(R.layout.hour_picker, null)
        val numberPicker = linearLayout.findViewById<NumberPicker>(R.id.hourPicker)
        numberPicker.wrapSelectorWheel = false

        //max between the next hour of today and the open hour of the center
        //otherwise the open hour
        numberPicker.minValue =
            if (DateUtils.isToday(c.timeInMillis)) {
                max(getDelayedCalendar()[Calendar.HOUR_OF_DAY], centerMinHour)
            } else {
                centerMinHour
            }
        numberPicker.maxValue = centerMaxHour-1
        numberPicker.value = c[Calendar.HOUR_OF_DAY]

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
            val date = changeDateToFull(dateChosen.text.toString())
            val hour = changeNumberToHour(numberPicker.value)
            val id = vm.findExistingReservation(date, hour)
            id.observe(life) { idReturned ->
                if (idReturned == null || idReturned == ownReservationId) {
                    timeNew.text = hour
                    c[Calendar.HOUR_OF_DAY] = numberPicker.value
                    //control the new couple date-time when i update the date
                    adjustDateDateCombination(c)
                    vm.changeSelectedDateTimeMillis(c.timeInMillis)
                } else {
                    builderFound.show()
                }
            }

        }
        val dialog = builder.create()
       /* val hourSelected= changeNumberToHour(numberPicker.value)
        val dateSelected = changeDateToFull(dateChosen.text.toString())
        //need to control if hour was setted by min
        if(timeNew.text!=hourSelected){
            val idRet = vm.findExistingReservation(dateSelected, hourSelected)
            idRet.observe(life) { idReturned ->
                //if yes, check if there is already a reservation
                if (idReturned == null || idReturned == ownReservationId) {
                    timeNew.text=hourSelected
                } else {
                    builderFound.show()
                }
            }
        }else*/
            dialog.show()
    }
}