package it.polito.mad.g26.playingcourtreservation.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.ViewContainer
import com.kizitonwose.calendar.view.WeekCalendarView
import com.kizitonwose.calendar.view.WeekDayBinder
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.adapter.ReservationsAdapter
import it.polito.mad.g26.playingcourtreservation.model.ReservationWithDetails
import it.polito.mad.g26.playingcourtreservation.util.VerticalSpaceItemDecoration
import it.polito.mad.g26.playingcourtreservation.util.displayDay
import it.polito.mad.g26.playingcourtreservation.util.displayText
import it.polito.mad.g26.playingcourtreservation.util.getWeekPageTitle
import it.polito.mad.g26.playingcourtreservation.util.makeGone
import it.polito.mad.g26.playingcourtreservation.util.makeInVisible
import it.polito.mad.g26.playingcourtreservation.util.makeVisible
import it.polito.mad.g26.playingcourtreservation.util.setTextColorRes
import it.polito.mad.g26.playingcourtreservation.util.setVisibility
import it.polito.mad.g26.playingcourtreservation.util.setupActionBar
import it.polito.mad.g26.playingcourtreservation.viewmodel.ReservationWithDetailsVM
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class ReservationsFragment : Fragment(R.layout.reservations_fragment) {

    private val reservationWithDetails by viewModels<ReservationWithDetailsVM>()

    private val reservationsAdapter = ReservationsAdapter()
    private val reservations = mutableMapOf<LocalDate, LiveData<List<ReservationWithDetails>>>()

    private val today = LocalDate.now()
    private var selectedDate: LocalDate = today
    private val reservationDatePattern = "dd-MM-yyyy"
    private val selectionFormatter = DateTimeFormatter.ofPattern("EEEE, d MMM yyyy")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBar(activity, "Reservations", false)

        // Setup WeekCalendarView
        var isInitialDateSet = false // Keep track of the initial selected date
        val weekCalendarView = view.findViewById<WeekCalendarView>(R.id.reservationsCalendarView)
        val currentMonth = YearMonth.now()
        val startDate = currentMonth.minusMonths(200).atStartOfMonth()
        val endDate = currentMonth.plusMonths(200).atEndOfMonth()
        configureBinders(view)
        weekCalendarView.setup(startDate, endDate, firstDayOfWeekFromLocale())
        weekCalendarView.scrollToDate(selectedDate)

        // Current Date Selected Text
        val selectedDateView =
            view.findViewById<TextView>(R.id.reservationsCalendarSelectedDateText)
        selectedDateView.text = selectionFormatter.format(selectedDate)

        // Current Month Selected Text
        val currentMonthView = view.findViewById<TextView>(R.id.calendarMonthYearText)
        weekCalendarView.weekScrollListener = weekScrollListener@{ weekDays ->
            currentMonthView.text = getWeekPageTitle(weekDays)
            // Update selectedDate to 7 days before or after
            if (!isInitialDateSet) {
                isInitialDateSet = true
                return@weekScrollListener
            }
            val newDate =
                if (weekDays.days.first().date.isBefore(selectedDate)) selectedDate.minusDays(7)
                else selectedDate.plusDays(7)
            selectedDate(selectedDateView, weekCalendarView, newDate)
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
        reservations.clear()
        reservationWithDetails.reservationWithDetails.observe(viewLifecycleOwner) {
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
            updateAdapterForDate(selectedDate)
        }

        // Set Up Reservations RecyclerView
        val reservationsRv = view.findViewById<RecyclerView>(R.id.reservationsRv)
        reservationsRv.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = reservationsAdapter
        }
        val itemDecoration =
            VerticalSpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.item_space_vertical))
        reservationsRv.addItemDecoration(itemDecoration)

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateAdapterForDate(date: LocalDate) {
        reservationsAdapter.updateData(reservations[date]?.value ?: emptyList())
        reservationsAdapter.notifyDataSetChanged()
    }

    private fun configureBinders(view: View) {
        val weekCalendarView = view.findViewById<WeekCalendarView>(R.id.reservationsCalendarView)
        val selectedDateTextView =
            view.findViewById<TextView>(R.id.reservationsCalendarSelectedDateText)
        val noReservationsTextView: TextView =
            view.findViewById(R.id.reservationsNoReservationsText)

        // Setup Day Container
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: WeekDay // Will be set when this container is bound
            val dateTextView: TextView = view.findViewById(R.id.reservationsCalendarDateText)
            val dayTextView: TextView = view.findViewById(R.id.reservationsCalendarDayText)
            val dotImageView: ImageView = view.findViewById(R.id.dayHasReservationImage)

            init {
                view.setOnClickListener {
                    selectedDate(selectedDateTextView, weekCalendarView, day.date)
                }
            }
        }

        weekCalendarView.dayBinder = object : WeekDayBinder<DayViewContainer> {
            // Called only when a new container is needed
            override fun create(view: View) = DayViewContainer(view)

            // Called every time we need to reuse a container
            override fun bind(container: DayViewContainer, data: WeekDay) {
                container.day = data
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
                        dotImageView.makeInVisible()
                        if (reservations[selectedDate]?.value != null) {
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

    private fun selectedDate(
        selectedDateTextView: TextView,
        weekCalendarView: WeekCalendarView,
        newDate: LocalDate
    ) {
        if (selectedDate == newDate)
            return
        val oldDate = selectedDate
        selectedDate = newDate
        weekCalendarView.notifyDateChanged(oldDate)
        weekCalendarView.notifyDateChanged(newDate)
        // update Reservations RV with new date
        selectedDateTextView.text = selectionFormatter.format(newDate)
        updateAdapterForDate(selectedDate)
    }

}