package com.fyc.android.hrapp

import android.os.Build
import android.os.Bundle
import android.service.controls.actions.BooleanAction
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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

@RequiresApi(Build.VERSION_CODES.O)
class CalendarFragment : Fragment(), CRV.onClickListener {

    private lateinit var _binding: FragmentCalendarBinding

    private lateinit var RV: RecyclerView

    private lateinit var selectedDate: LocalDate

    private lateinit var monthYearText: TextView

    private lateinit var daysInMonth: ArrayList<String>

    private val holidaysCollectionRef = Firebase.firestore.collection("holidays")

    private lateinit var hList: ArrayList<Holidays>

    private var boolean: Boolean = false

    private val c  = Calendar.getInstance()

    private val year  = c.get(Calendar.YEAR)

    private val m = c.get(Calendar.MONTH) + 1

    private val day = c.get(Calendar.DAY_OF_MONTH)

    private lateinit var month: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_calendar, container, false)

        hList = arrayListOf()

        daysInMonth = arrayListOf()

//        getLiveUpdates()

        month = ""

        if (m == 1) {
            month = "January"
        }
        if (m == 2) {
            month = "February"
        }
        if (m == 3) {
            month = "March"
        }
        if (m == 4) {
            month = "April"
        }
        if (m == 5) {
            month = "May"
        }
        if (m == 6) {
            month = "June"
        }
        if (m == 7) {
            month = "July"
        }
        if (m == 8) {
            month = "August"
        }
        if (m == 9) {
            month = "September"
        }
        if (m == 10) {
            month = "October"
        }
        if (m == 11) {
            month = "November"
        }
        if (m == 12) {
            month = "December"
        }

        initWidgets()

        selectedDate = LocalDate.now()

        setMonthView()

        getLiveUpdates()

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

//        getLiveUpdates()

        monthYearText.text = monthYearFromDate(selectedDate)

         daysInMonth = daysInMonthArray(selectedDate)

//        val calendarAdapter: CRV = CRV(this, daysInMonth, day,
//            "$month $year", _binding.monthYearTV, hList)

        val manager = GridLayoutManager(activity, 7)

        RV.setHasFixedSize(true)

        RV.layoutManager = manager
//        RV.adapter = calendarAdapter

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

    private fun getLiveUpdates(){

        holidaysCollectionRef.addSnapshotListener{querySnapshot, firebaseFirestoreException ->

            hList.clear()
            firebaseFirestoreException?.let {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }

            querySnapshot?.let {
                for (document in it){
                    val holiday = document.toObject<Holidays>()
                    hList.add(holiday)
                }

            }
            val calendarAdapter: CRV = CRV(this, daysInMonth, day,
                "$month $year", _binding.monthYearTV, hList)
            RV.adapter = calendarAdapter
        }
    }

    override fun onDayClick(day: String, position: Int) {
        if (day != "") {
//            Toast.makeText(requireContext(), "Selected Date  $day  ${monthYearFromDate(selectedDate)}", Toast.LENGTH_LONG).show()

            val dy = "$day ${monthYearFromDate(selectedDate)}"
//            for (h in hList){
//                boolean = h.day == dy
//            }
            val d = "$day  ${monthYearFromDate(selectedDate)}"
            if (hList.contains(Holidays(dy))) {
                boolean = true
                findNavController().navigate(
                    CalendarFragmentDirections.actionCalendarFragmentToAttendanceFragment(
                        d, boolean
                    )
                )
            } else {
                boolean = false
                findNavController().navigate(
                    CalendarFragmentDirections.actionCalendarFragmentToAttendanceFragment(
                        d, boolean
                    )
                )
            }





//            val dy = "$day ${monthYearFromDate(selectedDate)}"
//              Toast.makeText(requireContext(), d, Toast.LENGTH_LONG).show()
//            for (h in hList){
//                if (h.day == dy) {
//                    Toast.makeText(requireContext(), "This is a Holiday", Toast.LENGTH_LONG).show()
//                } else {
//                    navigate()
//                }
//            }

        }
    }

}