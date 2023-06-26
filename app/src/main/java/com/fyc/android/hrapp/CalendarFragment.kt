package com.fyc.android.hrapp

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fyc.android.hrapp.databinding.FragmentCalendarBinding
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class CalendarFragment : Fragment(), CRV.onClickListener {

    private lateinit var _binding: FragmentCalendarBinding

    private lateinit var RV: RecyclerView

    private lateinit var selectedDate: LocalDate

    private lateinit var monthYearText: TextView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_calendar, container, false)

        initWidgets()

        selectedDate = LocalDate.now()

        setMonthView()

        _binding.backBtn.setOnClickListener{
            previousMonthAction()
        }
        _binding.forwardBtn.setOnClickListener {
            nextMonthAction()
        }

        return _binding.root
    }

    fun initWidgets() {

        RV = _binding.calendarRecyclerView
        monthYearText = _binding.monthYearTV
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setMonthView() {

        monthYearText.text = monthYearFromDate(selectedDate)

        val daysInMonth: ArrayList<String> = daysInMonthArray(selectedDate)

        val calendarAdapter: CRV = CRV(this, daysInMonth)

        val manager = GridLayoutManager(activity, 7)

        RV.setHasFixedSize(true)

        RV.layoutManager = manager
        RV.adapter = calendarAdapter

    }

    fun daysInMonthArray(date: LocalDate): ArrayList<String>{

        val daysInMonthArray: ArrayList<String> = ArrayList<String>()

        val yearMonth: YearMonth = YearMonth.from(date)

        val daysInMonth: Int = yearMonth.lengthOfMonth()

        val firstOfMonth: LocalDate = selectedDate.withDayOfMonth(1)

        val dayOfWeek: Int = firstOfMonth.dayOfWeek.value

        for (i in 1 .. 42) {

            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {

                daysInMonthArray.add("")

            } else {

                daysInMonthArray.add("${i - dayOfWeek}")

            }

        }
        return daysInMonthArray
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun monthYearFromDate(date: LocalDate): String {

        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")

        return date.format(formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun previousMonthAction() {

        selectedDate = selectedDate.minusMonths(1)
        setMonthView()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun nextMonthAction() {

        selectedDate = selectedDate.plusMonths(1)
        setMonthView()
    }

    override fun onDayClick(day: String, position: Int) {
        if (day != ""){
//            Toast.makeText(requireContext(), "Selected Date  $day  ${monthYearFromDate(selectedDate)}", Toast.LENGTH_LONG).show()
            findNavController().navigate(CalendarFragmentDirections.actionCalendarFragmentToAttendanceFragment("$day  ${monthYearFromDate(selectedDate)}"))
        }
    }

}