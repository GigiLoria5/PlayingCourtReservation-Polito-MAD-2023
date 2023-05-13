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
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.fragment.searchFragments.SearchSportCentersFragmentDirections
import it.polito.mad.g26.playingcourtreservation.fragment.searchFragments.SearchSportCentersHomeFragmentDirections
import it.polito.mad.g26.playingcourtreservation.model.Sport
import it.polito.mad.g26.playingcourtreservation.model.custom.CourtWithDetails
import it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments.SearchCourtResultsVM
import java.util.Locale

object SearchCourtResultsUtil {

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
        hourTextView: TextView
    ) {

        dateTextView.text = getDateTimeFormatted(timeInMillis, dateFormat)
        hourTextView.text = getDateTimeFormatted(timeInMillis, hourFormat)
    }

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

    fun showDatePickerDialog(viewContext: Context, vm: SearchCourtResultsVM) {

        val c = Calendar.getInstance()
        c.timeInMillis = vm.selectedDateTimeMillis.value!!


        val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            c.apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, day)
            }
            adjustDateDateCombination(c)
            vm.changeSelectedDateTimeMillis(c.timeInMillis)
        }

        val datePickerDialog = DatePickerDialog(
            viewContext,
            datePicker,
            c[Calendar.YEAR],
            c[Calendar.MONTH],
            c[Calendar.DAY_OF_MONTH],
        )

        val minDate = getDelayedCalendar()
        datePickerDialog.datePicker.minDate = minDate.timeInMillis
        datePickerDialog.show()
    }

    fun showNumberPickerDialog(viewContext: Context, vm: SearchCourtResultsVM) {

        val c = Calendar.getInstance()
        c.timeInMillis = vm.selectedDateTimeMillis.value!!


        val linearLayout = LayoutInflater.from(viewContext).inflate(R.layout.hour_picker, null)
        val numberPicker = linearLayout.findViewById<NumberPicker>(R.id.hourPicker)
        numberPicker.wrapSelectorWheel = false

        //se la data è oggi ed orario=x, min value sarà un orario_futuro>x
        numberPicker.minValue =
            if (DateUtils.isToday(c.timeInMillis)) {
                getDelayedCalendar()[Calendar.HOUR_OF_DAY]
            } else
                0
        numberPicker.maxValue = 23
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
            c[Calendar.HOUR_OF_DAY] = numberPicker.value
            c[Calendar.MINUTE] = 0
            //Una volta che aggiorno la data, devo controllare la coppia data-ora per annullare possibili errori
            adjustDateDateCombination(c)
            vm.changeSelectedDateTimeMillis(c.timeInMillis)
        }
        val dialog = builder.create()
        dialog.show()
    }

    fun setAutoCompleteTextViewSport(
        viewContext: Context,
        sports: List<Sport>?,
        courtTypeACTV: AutoCompleteTextView,
        selectedSport: Int
    ) {
        val courtsType = mutableListOf("All")
        sports?.sortedBy { it.name }?.forEach { courtsType.add(it.name) }
        val adapterCourt =
            ArrayAdapter(viewContext, R.layout.list_item, courtsType)
        courtTypeACTV.setText(
            sports?.find { it.id == selectedSport }?.name ?: "All"
        )
        courtTypeACTV.setAdapter(adapterCourt)
    }

    fun navigateToAction(navController: NavController, city: String) {
        val direction =
            SearchSportCentersFragmentDirections.actionSearchSportCentersToSportCentersAction(
                city, "result"
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
                    "home", city
                )
            navController.navigate(direction)
        }
    }

    fun showConfirmReservationDialog(
        viewContext: Context,
        courtWithDetails: CourtWithDetails,
        vm: SearchCourtResultsVM
    ) {
        val timeInMillis = vm.selectedDateTimeMillis.value ?: 0
        var total: Float = courtWithDetails.court.hourCharge
        var selectedServicesString = ""
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
                        R.string.do_not_reserve_button
                    )
                ) { _, _ -> }
                .setOnCancelListener { }
        val dialog = builder.create()
        dialog.show()
    }
}