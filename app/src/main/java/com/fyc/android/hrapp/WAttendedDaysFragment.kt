package com.fyc.android.hrapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.view.isEmpty
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fyc.android.hrapp.databinding.FragmentWAttendedDaysBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import java.util.*
import kotlin.collections.ArrayList

class WAttendedDaysFragment : Fragment() {

    private lateinit var _binding: FragmentWAttendedDaysBinding

    private val dayCollectionRef = Firebase.firestore.collection("days")

    private lateinit var emList: ArrayList<Worker>

    private lateinit var eList: List<Worker>

    private lateinit var fList: List<Worker>

    private lateinit var employee: Worker

    private lateinit var RV: RecyclerView

    private val c  = Calendar.getInstance()

    private val year  = c.get(Calendar.YEAR)

    private val m = c.get(Calendar.MONTH) + 1

    private lateinit var month: String

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_w_attended_days, container, false)

        employee = arguments?.let { WAttendedDaysFragmentArgs.fromBundle(it).employee }!!

        _binding.eName.text = employee.fName + " " + employee.lName

        emList = arrayListOf()

        eList = listOf()

        fList = listOf()

        RV = _binding.wAttendedDaysList

        val manager = LinearLayoutManager(activity)

        RV.layoutManager = manager

        RV.setHasFixedSize(true)

        month = ""

        if (m == 1) {
            month = "Jan"
        }
        if (m == 2) {
            month = "Feb"
        }
        if (m == 3) {
            month = "Mar"
        }
        if (m == 4) {
            month = "Apr"
        }
        if (m == 5) {
            month = "May"
        }
        if (m == 6) {
            month = "Jun"
        }
        if (m == 7) {
            month = "Jul"
        }
        if (m == 8) {
            month = "Aug"
        }
        if (m == 9) {
            month = "Sep"
        }
        if (m == 10) {
            month = "Oct"
        }
        if (m == 11) {
            month = "Nov"
        }
        if (m == 12) {
            month = "Dec"
        }

        _binding.eFilter.text = month

        getDaysLiveUpdates()

        setHasOptionsMenu(true)

        return _binding.root
    }

    private fun getDaysLiveUpdates(){

        dayCollectionRef.addSnapshotListener{querySnapshot, firebaseFirestoreException ->

            emList.clear()
            firebaseFirestoreException?.let {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }

            querySnapshot?.let {
                for (document in it){
                    val worker = document.toObject<Worker>()
                    emList.add(worker)
                }
            }
            eList = emList.filter { it.nationalId == employee.nationalId && it.day.contains(month)
                    && it.day.contains(year.toString())}
            for (e in eList) {
                if(e.day[1].toString() == " ") {
                    e.day = "0" + e.day
                }
            }
            fList = eList.sortedBy { it.day.substring(0,2).toInt() }
            RV.adapter = ADRV(fList)

            if (fList.isEmpty()) {
                Toast.makeText(requireContext(),
                    employee.fName + " " + employee.lName + " Didn't Attend This Month",
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    fun filterAllYear() {
        eList = emList.filter { it.nationalId == employee.nationalId &&
                it.day.contains(year.toString())}
        for (e in eList) {
            if(e.day[1].toString() == " ") {
                e.day = "0" + e.day
            }
        }
        fList = eList.sortedBy { it.day.substring(0,2).toInt() }
        RV.adapter = ADRV(fList.sortedBy { it.day.substring(4,7) })

        _binding.eFilter.text = year.toString()

        if (fList.isEmpty()) {
            Toast.makeText(requireContext(),
                employee.fName + " " + employee.lName + " Didn't Attend This Year",
                Toast.LENGTH_LONG).show()
        }
    }

    fun filterMonth(month: String) {
        eList = emList.filter { it.nationalId == employee.nationalId && it.day.contains(month)
                && it.day.contains(year.toString())}
        for (e in eList) {
            if(e.day[1].toString() == " ") {
                e.day = "0" + e.day
            }
        }
        fList = eList.sortedBy { it.day.substring(0,2).toInt() }
        RV.adapter = ADRV(fList)

        _binding.eFilter.text = month

        if (fList.isEmpty()) {
            Toast.makeText(requireContext(),
                employee.fName + " " + employee.lName + " Didn't Attend This Month",
                Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.filter_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.jan -> filterMonth("Jan")
            R.id.feb -> filterMonth("Feb")
            R.id.mar -> filterMonth("Mar")
            R.id.apr -> filterMonth("Apr")
            R.id.may -> filterMonth("May")
            R.id.jun -> filterMonth("Jun")
            R.id.jul -> filterMonth("Jul")
            R.id.aug -> filterMonth("Aug")
            R.id.sep -> filterMonth("Sep")
            R.id.oct -> filterMonth("Oct")
            R.id.nov -> filterMonth("Nov")
            R.id.dec -> filterMonth("Dec")
            R.id.all_year -> filterAllYear()
            else -> return super.onContextItemSelected(item)
        }
        return true
    }

}