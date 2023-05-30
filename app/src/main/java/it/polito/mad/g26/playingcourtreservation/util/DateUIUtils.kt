package it.polito.mad.g26.playingcourtreservation.util

import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.newModel.Reservation
import java.time.LocalDate

fun handleReservationsDateUI(
    date: LocalDate,
    actualSelectedDate: LocalDate,
    dateTextView: TextView,
    dotImageView: ImageView,
    reservations: MutableMap<LocalDate, LiveData<List<Reservation>>>,
    noReservationsTextView: TextView,
    isNoReservationsTextViewInvisible: Boolean
) {
    val today = LocalDate.now()
    val colorToday = R.color.green_500
    val colorSelected = R.color.white
    val colorUnselected = R.color.black
    when (date) {
        actualSelectedDate -> {
            dateTextView.setTextColorRes(colorSelected)
            dateTextView.setBackgroundResource(R.drawable.calendar_selected_bg)
            if (today.isEqual(actualSelectedDate)) {
                dateTextView.setBackgroundResource(R.drawable.calendar_today_selected_bg)
            }
            dotImageView.makeInvisible()
            if (isNoReservationsTextViewInvisible) {
                noReservationsTextView.makeGone()
            } else {
                noReservationsTextView.makeVisible()
            }
        }

        today -> {
            dateTextView.setTextColorRes(colorToday)
            dateTextView.background = null
            dotImageView.setVisibility(
                reservations[date]?.value.orEmpty().isNotEmpty()
            )
        }

        else -> {
            dateTextView.setTextColorRes(colorUnselected)
            dateTextView.background = null
            dotImageView.setVisibility(
                reservations[date]?.value.orEmpty().isNotEmpty()
            )
        }
    }
}