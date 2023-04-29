package it.polito.mad.g26.playingcourtreservation.fragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.ViewContainer
import com.kizitonwose.calendar.view.WeekCalendarView
import com.kizitonwose.calendar.view.WeekDayBinder
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.activity.MainActivity
import it.polito.mad.g26.playingcourtreservation.util.displayDay
import it.polito.mad.g26.playingcourtreservation.util.displayText
import it.polito.mad.g26.playingcourtreservation.util.getWeekPageTitle
import it.polito.mad.g26.playingcourtreservation.viewmodel.ReservationVM
import java.time.LocalDate
import java.time.YearMonth

class ReservationsFragment : Fragment(R.layout.reservations_fragment) {
    private val reservationVM by viewModels<ReservationVM>()

    private val today = LocalDate.now()
    private var selectedDate: LocalDate = today

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Reservations"  // Update Title
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false) // Hide Back Button

        // Setup WeekCalendarView
        val weekCalendarView = view.findViewById<WeekCalendarView>(R.id.reservationsCalendarView)
        val currentMonth = YearMonth.now()
        val startDate = currentMonth.minusMonths(200).atStartOfMonth()
        val endDate = currentMonth.plusMonths(200).atEndOfMonth()
        configureBinders(view)
        weekCalendarView.setup(startDate, endDate, firstDayOfWeekFromLocale())
        weekCalendarView.scrollToDate(today)

        // Current Month Selected Text
        val currentMonthView = view.findViewById<TextView>(R.id.calendarMonthYearText)
        weekCalendarView.weekScrollListener = { weekDays ->
            currentMonthView.text = getWeekPageTitle(weekDays)
            // Update selectedDate to 7 days before or after
            selectedDate =
                if (weekDays.days.first().date.isBefore(selectedDate))
                    selectedDate.minusDays(7)
                else
                    selectedDate.plusDays(7)
        }

        // Navigate Between Weeks
        val previousWeekImage = view.findViewById<ImageView>(R.id.calendarPreviousImage)
        previousWeekImage.setOnClickListener {
            weekCalendarView.smoothScrollToDate(selectedDate.minusDays(7))
        }
        val nextWeekImage = view.findViewById<ImageView>(R.id.calendarNextImage)
        nextWeekImage.setOnClickListener {
            weekCalendarView.smoothScrollToDate(selectedDate.plusDays(7))
        }

        // Get All Reservations
        val reservations = reservationVM.reservations
        reservations.observe(viewLifecycleOwner) {
            println(it)
        }
        // TODO: Show Reservations
    }

    private fun configureBinders(view: View) {
        val calendarView = view.findViewById<WeekCalendarView>(R.id.reservationsCalendarView)

        // Setup Day Container
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: WeekDay // Will be set when this container is bound
            val dateTextView: TextView = view.findViewById(R.id.reservationsCalendarDateText)
            val dayTextView: TextView = view.findViewById(R.id.reservationsCalendarDayText)

            init {
                view.setOnClickListener {
                    if (selectedDate != day.date) {
                        // val oldDate = selectedDate
                        selectedDate = day.date
                        println(selectedDate)
                        calendarView.notifyDateChanged(day.date)
                        // oldDate?.let { calendarView.notifyDateChanged(it) }
                        // TODO: update Reservations RV with new date
                    }
                }
            }

        }

        calendarView.dayBinder = object : WeekDayBinder<DayViewContainer> {
            // Called only when a new container is needed
            override fun create(view: View) = DayViewContainer(view)

            // Called every time we need to reuse a container
            override fun bind(container: DayViewContainer, data: WeekDay) {
                container.day = data
                container.dayTextView.text = data.date.dayOfWeek.displayText()
                container.dateTextView.text = data.date.displayDay()
                // TODO: change colors or other things
            }
        }

    }

}