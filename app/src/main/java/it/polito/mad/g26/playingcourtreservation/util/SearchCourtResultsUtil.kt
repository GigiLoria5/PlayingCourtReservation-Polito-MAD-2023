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
import it.polito.mad.g26.playingcourtreservation.fragment.searchFragments.SearchCourtResultsFragmentDirections
import it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments.SearchCourtResultsVM
import it.polito.mad.g26.playingcourtreservation.fragment.searchFragments.SearchCourtFragmentDirections
import it.polito.mad.g26.playingcourtreservation.model.Sport
import it.polito.mad.g26.playingcourtreservation.model.custom.CourtWithDetails

import java.util.*

object SearchCourtResultsUtil {

    fun getMockInitialDateTime(): Calendar {
        val c = getDelayedCalendar()
        c.set(Calendar.MINUTE, 0)
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
            mySelectionCalendar.set(
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH),
                c.get(Calendar.HOUR_OF_DAY),
                0
            )
        }
    }

    fun showDatePickerDialog(viewContext: Context, vm: SearchCourtResultsVM) {

        val c = Calendar.getInstance()
        c.timeInMillis = vm.selectedDateTimeMillis.value!!


        val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            c.set(year, month, day)

            adjustDateDateCombination(c)
            vm.changeSelectedDateTimeMillis(c.timeInMillis)
        }

        val datePickerDialog = DatePickerDialog(
            viewContext,
            datePicker,
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH),
            c.get(Calendar.DAY_OF_MONTH),
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
                getDelayedCalendar().get(Calendar.HOUR_OF_DAY)
            } else
                0
        numberPicker.maxValue = 23
        numberPicker.value = c.get(Calendar.HOUR_OF_DAY)

        numberPicker.displayedValues = (0..23).toList().map { if (it < 10) "0$it:00" else "$it:00" }
            .slice(numberPicker.minValue..23).toTypedArray()

        val builder = AlertDialog.Builder(viewContext)
        builder.setTitle("Seleziona orario")
        builder.setMessage("Puoi prenotare un campo al massimo 30 minuti prima della partita")
        builder.setView(linearLayout)
        builder.setPositiveButton("OK") { _, _ ->
            c.set(Calendar.HOUR_OF_DAY, numberPicker.value)

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
            SearchCourtResultsFragmentDirections.actionSearchCourtResultsFragmentToSearchCourtActionFragment(
                "result", city
            )
        navController.navigate(direction)

    }

    fun navigateBack(navController: NavController, city: String, bornFrom: String) {
        if (bornFrom == "home")
            navController.popBackStack()
        else {
            navController.popBackStack()
            val direction =
                SearchCourtFragmentDirections.actionSearchCourtFragmentToSearchCourtActionFragment(
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
                .setTitle("Reserve this court")
                .setMessage(reservationConfirmationText)
                .setPositiveButton("Reserve") { _, _ ->
                    vm.reserveCourt(
                        courtWithDetails.court.id,
                        total,
                        selectedServicesIds
                    )
                    /*TODO NAVIGA A RESERVATION*/
                }.setNegativeButton("Don't reserve") { _, _ -> }
                .setOnCancelListener() { }
        val dialog = builder.create()
        dialog.show()
    }
}