package it.polito.mad.g26.playingcourtreservation.fragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.activity.MainActivity
import it.polito.mad.g26.playingcourtreservation.util.displayText
import it.polito.mad.g26.playingcourtreservation.viewmodel.ReservationVM
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

class ReservationsFragment : Fragment(R.layout.reservations_fragment) {
    private val reservationVM by viewModels<ReservationVM>()

    private var selectedDate: LocalDate? = null
    private val today = LocalDate.now()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Reservations"  // Update Title
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false) // Hide Back Button

        // Setup CalendarView
        val calendarView = view.findViewById<CalendarView>(R.id.reservationsCalendarView)
        val daysOfWeek = daysOfWeek()
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(200)
        val endMonth = currentMonth.plusMonths(200)
        configureBinders(view, daysOfWeek)
        calendarView.setup(startMonth, endMonth, daysOfWeek.first())
        calendarView.scrollToMonth(currentMonth)

        // Current Month Selected Text
        val currentMonthView = view.findViewById<TextView>(R.id.calendarMonthYearText)
        calendarView.monthScrollListener = { month ->
            currentMonthView.text = month.yearMonth.displayText()
            // Clear selection if we scroll to a new month
            selectedDate?.let {
                selectedDate = null
            }
        }

        // Navigate Between Months
        val previousMonthImage = view.findViewById<ImageView>(R.id.calendarPreviousMonthImage)
        previousMonthImage.setOnClickListener {
            calendarView.findFirstVisibleMonth()?.let {
                calendarView.smoothScrollToMonth(it.yearMonth.previousMonth)
            }
        }
        val nextMonthImage = view.findViewById<ImageView>(R.id.calendarNextMonthImage)
        nextMonthImage.setOnClickListener {
            calendarView.findFirstVisibleMonth()?.let {
                calendarView.smoothScrollToMonth(it.yearMonth.nextMonth)
            }
        }

        // Get All Reservations
        val reservations = reservationVM.reservations
        reservations.observe(viewLifecycleOwner) {
            println(it)
        }
    }

    private fun configureBinders(view: View, daysOfWeek: List<DayOfWeek>) {
        val calendarView = view.findViewById<CalendarView>(R.id.reservationsCalendarView)

        // Setup Day Container
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val dayTextView: TextView = view.findViewById(R.id.reservationsCalendarDayText)

            init {
                view.setOnClickListener {
                    if (day.position == DayPosition.MonthDate && selectedDate != day.date) {
                        selectedDate = day.date
                        println(selectedDate)
                        calendarView.notifyDateChanged(day.date)
                        // TODO: update Reservations RV with new date
                    }
                }
            }
        }

        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            // Called only when a new container is needed.
            override fun create(view: View) = DayViewContainer(view)

            // Called every time we need to reuse a container.
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.dayTextView.text = data.date.dayOfMonth.toString()
            }
        }

        // Setup Month Headers
        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout: LinearLayout = view.findViewById(R.id.calendarLegendLayout)
        }

        calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                if (container.legendLayout.tag == null) {
                    container.legendLayout.tag = data.yearMonth
                    container.legendLayout.children.map { it as TextView }
                        .forEachIndexed { index, tv ->
                            tv.text = daysOfWeek[index].displayText(uppercase = true)
                        }
                }
            }
        }

    }

}