package com.fyc.android.hrapp

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fyc.android.hrapp.databinding.FragmentSalaryBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.Calendar

class SalaryFragment : Fragment(), SRV.onClickListener {

    private lateinit var _binding: FragmentSalaryBinding

    private val workerCollectionRef = Firebase.firestore.collection("workers")

    private val dayCollectionRef = Firebase.firestore.collection("days")

    private val salaryCollectionRef = Firebase.firestore.collection("salary")

    private val holidaysCollectionRef = Firebase.firestore.collection("holidays")

    private lateinit var RV: RecyclerView

    private lateinit var wList: ArrayList<Worker>

    private lateinit var dList: ArrayList<Worker>

    private lateinit var msList: ArrayList<MonthSalary>

    private lateinit var hList: ArrayList<Holidays>

    private val c  = Calendar.getInstance()

    private val year  = c.get(Calendar.YEAR)

    private val m = c.get(Calendar.MONTH) + 1

    private val day = c.get(Calendar.DAY_OF_MONTH)

    private lateinit var month: String

    private var msSalary: Int = 0

    private var dSalary: Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_salary, container, false)

        RV = _binding.wSalaryList

        val manager = LinearLayoutManager(activity)

        RV.layoutManager = manager

        RV.setHasFixedSize(true)

        wList = arrayListOf()

        dList = arrayListOf()

        msList = arrayListOf()

        hList = arrayListOf()

//        val c  = Calendar.getInstance()
//
//        val year  = c.get(Calendar.YEAR)
//        val m = c.get(Calendar.MONTH)

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
//        val day = c.get(Calendar.DAY_OF_MONTH)

//        wList.filter { it.day == "$day $month $year"}

//        for ( worker in wList) {
//            if (worker.day == "$day $month $year" ) {
//
//                val hourSalary = worker.salary.toInt() / 21 / 24
//                val dayHours = worker.lTime - worker.aTime
//                val daySalary = hourSalary  * dayHours
//                val mSalary = daySalary * days
//
//            }
//        }

        getMonthLiveUpdates()

        getLiveUpdates()

        getDaysLiveUpdates()

        getLiveUpdatesForHolidays()

        return _binding.root
    }

    fun getEditedWorker(worker: Worker, days: Int): Map<String, Any> {

        val hourSalary = worker.salary.toInt() / 21 / 24
        val dayHours = worker.lTime - worker.aTime
        val daySalary = hourSalary  * dayHours
        val mSalary = daySalary * days
        val map = mutableMapOf<String, Any>()
        if (mSalary != null ){
            map["msalary"] = mSalary
        }
        return map
    }

    private fun saveMonthlySalary(monthSalary: MonthSalary) = CoroutineScope(Dispatchers.IO).launch {

        try {
            salaryCollectionRef.add(monthSalary).await()
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(),"Successfully Added The Meal",Toast.LENGTH_LONG).show()
            }
        } catch (e:Exception){
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(),e.message,Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getLiveUpdatesForHolidays(){

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
        }
    }

    private fun getLiveUpdates(){

        workerCollectionRef.addSnapshotListener{querySnapshot, firebaseFirestoreException ->

            wList.clear()
            firebaseFirestoreException?.let {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }

            querySnapshot?.let {
                for (document in it){
                    val worker = document.toObject<Worker>()
//                    if (wList.find { it.fName == worker.fName }?.fName == worker.fName)
                    wList.add(worker)
                    RV.adapter = SRV(this, wList, msList, month)
                }

            }
        }
    }

    private fun getDaysLiveUpdates(){

        dayCollectionRef.addSnapshotListener{querySnapshot, firebaseFirestoreException ->

            dList.clear()
            firebaseFirestoreException?.let {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }

            querySnapshot?.let {
                for (document in it){
                    val worker = document.toObject<Worker>()
//                    if (wList.find { it.fName == worker.fName }?.fName == worker.fName)
                    dList.add(worker)

                }

            }
        }
    }

    private fun getMonthLiveUpdates(){

        salaryCollectionRef.addSnapshotListener{querySnapshot, firebaseFirestoreException ->

            msList.clear()
            firebaseFirestoreException?.let {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }

            querySnapshot?.let {
                for (document in it){
                    val worker = document.toObject<MonthSalary>()
//                    if (wList.find { it.fName == worker.fName }?.fName == worker.fName)
                    msList.add(worker)

                }

            }
        }
    }

    fun updateWorkerDailySalary(worker: Worker, dSalary: String) = CoroutineScope(Dispatchers.IO).launch {
        val workerQuery = dayCollectionRef
            .whereEqualTo("day", worker.day)
            .whereEqualTo("fname", worker.fName)
            .whereEqualTo("lname", worker.lName)
            .whereEqualTo("dob", worker.dOB)
            .whereEqualTo("salary", worker.salary)
            .whereEqualTo("pnumber", worker.pNumber)
            .get()
            .await()
        if (workerQuery.documents.isNotEmpty()){
            for (document in workerQuery){
                try {
                    dayCollectionRef.document(document.id).update("dsalary", dSalary)

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
//                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }

        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "No Worker Matched The Query", Toast.LENGTH_LONG).show()
            }
        }
    }

//    fun updateWorkerMonthlySalary(worker: Worker, mSalary: String) = CoroutineScope(Dispatchers.IO).launch {
//        val workerQuery = workerCollectionRef
//            .whereEqualTo("fname", worker.fName)
//            .whereEqualTo("lname", worker.lName)
//            .whereEqualTo("dob", worker.dOB)
//            .whereEqualTo("salary", worker.salary)
//            .whereEqualTo("pnumber", worker.pNumber)
//            .get()
//            .await()
//        if (workerQuery.documents.isNotEmpty()){
//            for (document in workerQuery){
//                try {
//                    dayCollectionRef.document(document.id).update("msalary", mSalary)
//
//                } catch (e: Exception) {
//                    withContext(Dispatchers.Main) {
////                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
//                    }
//                }
//            }
//
//        } else {
//            withContext(Dispatchers.Main) {
//                Toast.makeText(requireContext(), "No Worker Matched The Query", Toast.LENGTH_LONG).show()
//            }
//        }
//    }

    fun updateWorkerMonthlySalary(monthSalary: MonthSalary, mSalary: String) = CoroutineScope(Dispatchers.IO).launch {
        val workerQuery = salaryCollectionRef
            .whereEqualTo("fname", monthSalary.fName)
            .whereEqualTo("lname", monthSalary.lName)
            .whereEqualTo("dob", monthSalary.dOB)
            .whereEqualTo("month", monthSalary.month)
            .get()
            .await()
        if (workerQuery.documents.isNotEmpty()){
            for (document in workerQuery){
                try {
                    salaryCollectionRef.document(document.id).update("salary", mSalary)

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
//                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }

        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "No Worker Matched The Query", Toast.LENGTH_LONG).show()
                saveMonthlySalary(monthSalary)
            }
        }
    }

    override fun onItemClick(position: Int) {

        val employee = wList[position]
//        var monthSalary = 0
        msSalary = 0

//        if (dList.contains(Worker(employee.fName,employee.lName,employee.dOB,employee.salary))){
//            Toast.makeText(requireContext(), month, Toast.LENGTH_LONG).show()
//        }

        for (w in dList) {
            if (w.fName == employee.fName && w.lName == employee.lName && w.dOB == employee.dOB &&
                    w.day.contains(month)) {
                val hourSalary = w.salary.toInt() / 21 / 24
                val dayHours = w.lTime - w.aTime
                val daySalary = hourSalary  * dayHours
                updateWorkerDailySalary(w, daySalary.toString())
//                dSalary = w.dSalary.toInt()
            }
        }

        for (w in dList) {
            if (w.fName == employee.fName && w.lName == employee.lName && w.dOB == employee.dOB &&
                w.day.contains(month)) {
                msSalary+= w.dSalary.toInt()
            }
        }

        for (w in msList) {
            if (w.fName == employee.fName && w.lName == employee.lName && w.dOB == employee.dOB &&
                w.month == month && w.daysoff != "" && w.bonus != "" && w.deduction != "") {
                if (w.daysoff.toInt() >= 1) {
                    val hourSalary = employee.salary.toInt() / 21 / 24
                    val daySalary = hourSalary  * 8
                    val s = msSalary + daySalary + w.bonus.toInt() - w.deduction.toInt()
                    msSalary = s
                } else {
                    val s = msSalary + w.bonus.toInt() - w.deduction.toInt()
                    msSalary = s
                }
            }
        }

        var s = 0

        for (h in hList) {
            if (h.day.contains(month) && h.day.contains(year.toString())) {
                val hourSalary = employee.salary.toInt() / 21 / 24
                val daySalary = hourSalary  * 8
                s += 1
                msSalary += (s * daySalary)
            }
        }

        findNavController().navigate(SalaryFragmentDirections
            .actionSalaryFragmentToSalaryDetailsFragment(employee, month, msSalary.toString()))

//        if (msList.toString().contains(employee.fName) &&
//            msList.toString().contains(employee.lName) && msList.toString().contains(employee.dOB)){
//            for (w in dList) {
//                if (w.fName == employee.fName && w.lName == employee.lName &&
//                    w.dOB == employee.dOB && w.day.contains(month)) {
//                    if (msList)
//                    msSalary += w.dSalary.toInt()
//                    updateWorkerMonthlySalary(MonthSalary(w.fName, w.lName, w.dOB, month),
//                        msSalary.toString())
//
//                }
//            }
//        } else {
//            saveMonthlySalary(MonthSalary(employee.fName, employee.lName, employee.dOB, month))
//        }



//        for (w in dList) {
//            if (w.fName == employee.fName && w.lName == employee.lName && w.dOB == employee.dOB) {
//                //for (ms in msList) {
////                    if (ms.fName == w.fName && ms.lName == w.lName && ms.dOB == w.dOB &&
////                        ms.month == month
////                    ) {
//                if (msList.toString().contains(w.fName) && msList.toString().contains(w.lName) &&
//                        msList.toString().contains(w.dOB) && msList.toString().contains(month)) {
//                        msSalary += w.dSalary.toInt()
////                        Toast.makeText(requireContext(), msSalary.toString(), Toast.LENGTH_LONG)
////                            .show()
//                        updateWorkerMonthlySalary(ms, msSalary.toString())
//                        RV.adapter = SRV(this, wList, msList, month)
//                } else {
//                        updateWorkerMonthlySalary(
//                            MonthSalary(w.fName, w.lName, w.dOB, month),
//                            msSalary.toString()
//                        )
////                        getMonthLiveUpdates()
//                    }
//                //
//            }
//        }

//        Toast.makeText(requireContext(), msList.toString(), Toast.LENGTH_LONG).show()

//        for (ms in msList) {
//            if (ms.fName == employee.fName && ms.lName == employee.lName && ms.dOB== employee.dOB &&
//                ms.month == month) {
//                msSalary += dSalary
//                updateWorkerMonthlySalary(ms, msSalary.toString())
//                RV.adapter = SRV(this, wList, msList, month)
//            } else {
//                saveMonthlySalary(MonthSalary(employee.fName, employee.lName, employee.dOB, month))
//                getMonthLiveUpdates()
//            }
//        }

//        for (w in dList) {
//            if (w.fName == employee.fName && w.lName == employee.lName && w.dOB == employee.dOB) {
//
//                if (w.day == "$day $month $year") {
//
//                    val hourSalary = w.salary.toInt() / 21 / 24
//                    val dayHours = w.lTime - w.aTime
//                    val daySalary = hourSalary  * dayHours
//
//                    updateWorkerDailySalary(w, daySalary.toString())
//                }
//                val wDList = dList.filter { it.day.contains("$month $year") &&
//                        it.fName == w.fName && it.lName == w.lName && it.dOB == w.dOB }
//
//                    for (w in wDList) {
//                        monthSalary+= w.dSalary.toInt()
//                        updateWorkerMonthlySalary(w,monthSalary.toString())
//                    }
//            }
//        }



    }

}