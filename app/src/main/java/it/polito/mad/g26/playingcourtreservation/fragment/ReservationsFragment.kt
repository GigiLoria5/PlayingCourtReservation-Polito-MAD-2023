package it.polito.mad.g26.playingcourtreservation.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import com.kizitonwose.calendar.view.WeekCalendarView
import com.kizitonwose.calendar.view.WeekDayBinder
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.adapter.ReservationsAdapter
import it.polito.mad.g26.playingcourtreservation.model.Reservation
import it.polito.mad.g26.playingcourtreservation.model.ReservationWithDetails
import it.polito.mad.g26.playingcourtreservation.util.VerticalSpaceItemDecoration
import it.polito.mad.g26.playingcourtreservation.util.displayDay
import it.polito.mad.g26.playingcourtreservation.util.displayText
import it.polito.mad.g26.playingcourtreservation.util.getWeekPageTitle
import it.polito.mad.g26.playingcourtreservation.util.makeGone
import it.polito.mad.g26.playingcourtreservation.util.makeInvisible
import it.polito.mad.g26.playingcourtreservation.util.makeVisible
import it.polito.mad.g26.playingcourtreservation.util.setTextColorRes
import it.polito.mad.g26.playingcourtreservation.util.setVisibility
import it.polito.mad.g26.playingcourtreservation.util.setupActionBar
import it.polito.mad.g26.playingcourtreservation.util.startShimmerAnimation
import it.polito.mad.g26.playingcourtreservation.util.stopShimmerAnimation
import it.polito.mad.g26.playingcourtreservation.viewmodel.ReservationWithDetailsVM
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Calendar.MONTH
import java.util.Calendar.WEEK_OF_MONTH

class ReservationsFragment : Fragment(R.layout.reservations_fragment) {

    private val reservationWithDetailsVM by viewModels<ReservationWithDetailsVM>()

    private val reservationsAdapter = ReservationsAdapter()
    private val reservations = mutableMapOf<LocalDate, LiveData<List<ReservationWithDetails>>>()

    private val today = LocalDate.now()
    private var selectedDate: LocalDate = today
    private val reservationDatePattern = Reservation.getReservationDatePattern()
    private val selectionFormatter = DateTimeFormatter.ofPattern("EEEE, d MMM yyyy")
    private var calendarViewActive = WEEK_OF_MONTH

    private lateinit var monthCalendarView: CalendarView
    private lateinit var weekCalendarView: WeekCalendarView
    private lateinit var reservationsRv: RecyclerView
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private lateinit var selectedDateTextView: TextView
    private lateinit var noReservationsTextView: TextView
    private lateinit var currentMonthView: TextView

    var isSetupFinished = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBar(activity, "Reservations", false)

        // Setup late init variables
        monthCalendarView = view.findViewById(R.id.reservationsMonthCalendarView)
        weekCalendarView = view.findViewById(R.id.reservationsCalendarView)
        reservationsRv = view.findViewById(R.id.reservationsRv)
        shimmerFrameLayout = view.findViewById(R.id.reservationsShimmerView)
        selectedDateTextView = view.findViewById(R.id.reservationsCalendarSelectedDateText)
        noReservationsTextView = view.findViewById(R.id.reservationsNoReservationsText)
        currentMonthView = view.findViewById(R.id.calendarMonthYearText)

        // Setup Calendar Views
        val currentMonth = YearMonth.now()
        val startDate = currentMonth.minusMonths(200).atStartOfMonth()
        val endDate = currentMonth.plusMonths(200).atEndOfMonth()
        val startMonth = currentMonth.minusMonths(200)
        val endMonth = currentMonth.plusMonths(200)
        configureBinders(view)
        weekCalendarView.setup(startDate, endDate, firstDayOfWeekFromLocale())
        weekCalendarView.scrollToDate(selectedDate)
        monthCalendarView.setup(startMonth, endMonth, firstDayOfWeekFromLocale())
        monthCalendarView.scrollToMonth(currentMonth)

        // Start Shimmer loading
        shimmerFrameLayout.startShimmerAnimation(reservationsRv)

        // Current Date Selected Text
        selectedDateTextView.text = selectionFormatter.format(selectedDate)
        updateSelectedDate(selectedDate)

        // Current Month Selected Text
        currentMonthView.setOnClickListener { // onClick switch Calendar View
            switchCalendarView()
        }
        weekCalendarView.weekScrollListener = weekScrollListener@{ weekDays ->
            currentMonthView.text = getWeekPageTitle(weekDays)
        }
        monthCalendarView.monthScrollListener = { month ->
            currentMonthView.text = month.yearMonth.displayText()
        }

        // Handle Calendar Navigation
        val calendarPreviousBtn = view.findViewById<ImageView>(R.id.calendarPreviousImage)
        calendarPreviousBtn.setOnClickListener {
            if (calendarViewActive == WEEK_OF_MONTH) {
                updateSelectedDate(selectedDate.minusDays(7))
                weekCalendarView.smoothScrollToDate(selectedDate)
            } else {
                monthCalendarView.findFirstVisibleMonth()?.let {
                    monthCalendarView.smoothScrollToMonth(it.yearMonth.previousMonth)
                }
            }
        }
        val calendarNextBtn = view.findViewById<ImageView>(R.id.calendarNextImage)
        calendarNextBtn.setOnClickListener {
            if (calendarViewActive == WEEK_OF_MONTH) {
                updateSelectedDate(selectedDate.plusDays(7))
                weekCalendarView.smoothScrollToDate(selectedDate)
            } else {
                monthCalendarView.findFirstVisibleMonth()?.let {
                    monthCalendarView.smoothScrollToMonth(it.yearMonth.nextMonth)
                }
            }
        }

        // Get All Reservations
        reservationWithDetailsVM.reservationWithDetails.observe(viewLifecycleOwner) {
            reservations.clear()
            it.forEach { reservationWithDetails ->
                val localDate = LocalDate.parse(
                    reservationWithDetails.reservation.date,
                    DateTimeFormatter.ofPattern(reservationDatePattern)
                )
                val currentList = reservations[localDate]?.value ?: emptyList()
                val updatedList = MutableLiveData(currentList + reservationWithDetails)
                reservations[localDate] = updatedList
                if (currentList.isNotEmpty()) weekCalendarView.notifyDateChanged(localDate) // To show dotView
            }
            isSetupFinished = true
            configureBinders(view)
            updateSelectedDate(selectedDate) // Force update
            updateAdapterForDate(selectedDate)
            // Stop Shimmer loading
            Handler(Looper.getMainLooper()).postDelayed({
                shimmerFrameLayout.stopShimmerAnimation(reservationsRv)
            }, 1000)
        }

        // Set Up Reservations RecyclerView
        reservationsRv.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = reservationsAdapter
        }
        val itemDecoration =
            VerticalSpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.item_space_vertical))
        reservationsRv.addItemDecoration(itemDecoration)
    }

    private fun switchCalendarView() {
        if (calendarViewActive == WEEK_OF_MONTH) {
            reservationsRv.makeGone()
            noReservationsTextView.makeGone()
            selectedDateTextView.makeGone()
            weekCalendarView.makeGone()
            val yearMonth = YearMonth.from(selectedDate)
            currentMonthView.text = yearMonth.displayText()
            monthCalendarView.scrollToMonth(yearMonth)
            monthCalendarView.makeVisible()
            calendarViewActive = MONTH
            return
        }
        monthCalendarView.makeGone()
        currentMonthView.text = getWeekPageTitle(selectedDate)
        weekCalendarView.scrollToDate(selectedDate)
        updateSelectedDate(selectedDate) // Force update
        weekCalendarView.makeVisible()
        selectedDateTextView.makeVisible()
        reservationsRv.makeVisible()
        calendarViewActive = WEEK_OF_MONTH
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateAdapterForDate(date: LocalDate) {
        reservationsAdapter.updateData(reservations[date]?.value ?: emptyList())
        reservationsAdapter.notifyDataSetChanged()
    }

    private fun updateSelectedDate(newDate: LocalDate) {
        val oldDate = selectedDate
        selectedDate = newDate
        // Update UI
        weekCalendarView.notifyDateChanged(oldDate)
        monthCalendarView.notifyDateChanged(oldDate)
        weekCalendarView.notifyDateChanged(newDate)
        monthCalendarView.notifyDateChanged(newDate)
        selectedDateTextView.text = selectionFormatter.format(newDate)
        updateAdapterForDate(selectedDate) // update Reservations RV with new date
    }

    private fun configureBinders(view: View) {
        val weekCalendarView = view.findViewById<WeekCalendarView>(R.id.reservationsCalendarView)
        val monthCalendarView = view.findViewById<CalendarView>(R.id.reservationsMonthCalendarView)
        val noReservationsTextView: TextView =
            view.findViewById(R.id.reservationsNoReservationsText)

        class WeekDayViewContainer(view: View) : ViewContainer(view) {
            lateinit var weekDay: WeekDay // Will be set when this container is bound
            val dateTextView: TextView = view.findViewById(R.id.reservationsCalendarDateText)
            val dayTextView: TextView = view.findViewById(R.id.reservationsCalendarDayText)
            val dotImageView: ImageView = view.findViewById(R.id.dayHasReservationImage)

            init {
                view.setOnClickListener {
                    updateSelectedDate(weekDay.date)
                    weekCalendarView.smoothScrollToDate(selectedDate)
                }
            }
        }

        class CalendarDayViewContainer(view: View) : ViewContainer(view) {
            lateinit var calendarDay: CalendarDay // Will be set when this container is bound.
            val dateTextView: TextView = view.findViewById(R.id.reservationsCalendarDateText)
            val dayTextView: TextView = view.findViewById(R.id.reservationsCalendarDayText)
            val dotImageView: ImageView = view.findViewById(R.id.dayHasReservationImage)

            init {
                view.setOnClickListener {
                    updateSelectedDate(calendarDay.date)
                    switchCalendarView()
                }
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout: LinearLayout = view.findViewById(R.id.legendLayout)
        }

        // Setup Month Container
        monthCalendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                if (container.legendLayout.tag == null) {
                    container.legendLayout.tag = data.yearMonth
                    container.legendLayout.children.map { it as TextView }
                        .forEachIndexed { index, tv ->
                            val daysOfWeek = daysOfWeek()
                            tv.text = daysOfWeek[index].displayText()
                            tv.setTextColorRes(R.color.black)
                        }
                }
            }
        }

        // Setup Day Container
        monthCalendarView.dayBinder = object : MonthDayBinder<CalendarDayViewContainer> {
            override fun create(view: View) = CalendarDayViewContainer(view)
            override fun bind(container: CalendarDayViewContainer, data: CalendarDay) {
                container.calendarDay = data
                container.dayTextView.makeGone()
                val dateTextView = container.dateTextView
                val dotImageView = container.dotImageView
                dateTextView.text = data.date.displayDay()
                // Hide inDates and outDates
                if (data.position != DayPosition.MonthDate) {
                    dateTextView.makeInvisible()
                    dotImageView.makeInvisible()
                    return
                }
                // Handle Date UI
                val colorToday = R.color.green_500
                val colorSelected = R.color.white
                val colorUnselected = R.color.black
                when (data.date) {
                    selectedDate -> {
                        dateTextView.setTextColorRes(colorSelected)
                        dateTextView.setBackgroundResource(R.drawable.calendar_selected_bg)
                        if (today.isEqual(selectedDate)) {
                            dateTextView.setBackgroundResource(R.drawable.calendar_today_selected_bg)
                        }
                        dotImageView.makeInvisible()
                    }

                    today -> {
                        dateTextView.setTextColorRes(colorToday)
                        dateTextView.background = null
                        dotImageView.setVisibility(
                            reservations[data.date]?.value.orEmpty().isNotEmpty()
                        )
                    }

                    else -> {
                        dateTextView.setTextColorRes(colorUnselected)
                        dateTextView.background = null
                        dotImageView.setVisibility(
                            reservations[data.date]?.value.orEmpty().isNotEmpty()
                        )
                    }
                }
            }
        }

        weekCalendarView.dayBinder = object : WeekDayBinder<WeekDayViewContainer> {
            override fun create(view: View) = WeekDayViewContainer(view)
            override fun bind(container: WeekDayViewContainer, data: WeekDay) {
                container.weekDay = data
                val dayTextView = container.dayTextView
                val dateTextView = container.dateTextView
                val dotImageView = container.dotImageView
                dayTextView.text = data.date.dayOfWeek.displayText()
                dateTextView.text = data.date.displayDay()
                // Handle Date UI
                val colorToday = R.color.green_500
                val colorSelected = R.color.white
                val colorUnselected = R.color.black
                when (data.date) {
                    selectedDate -> {
                        dateTextView.setTextColorRes(colorSelected)
                        dateTextView.setBackgroundResource(R.drawable.calendar_selected_bg)
                        if (today.isEqual(selectedDate)) {
                            dateTextView.setBackgroundResource(R.drawable.calendar_today_selected_bg)
                        }
                        dotImageView.makeInvisible()
                        if (!isSetupFinished || reservations[selectedDate]?.value != null) {
                            noReservationsTextView.makeGone()
                        } else {
                            noReservationsTextView.makeVisible()
                        }
                    }

                    today -> {
                        dateTextView.setTextColorRes(colorToday)
                        dateTextView.background = null
                        dotImageView.setVisibility(
                            reservations[data.date]?.value.orEmpty().isNotEmpty()
                        )
                    }

                    else -> {
                        dateTextView.setTextColorRes(colorUnselected)
                        dateTextView.background = null
                        dotImageView.setVisibility(
                            reservations[data.date]?.value.orEmpty().isNotEmpty()
                        )
                    }
                }
            }
        }
    }
}