package com.fyc.android.hrapp

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fyc.android.hrapp.databinding.FragmentSalaryBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class SalaryFragment : Fragment(), SRV.onClickListener {

    private lateinit var _binding: FragmentSalaryBinding

    private lateinit var animator1: LoadingAnimation

    private val workerCollectionRef = Firebase.firestore.collection("workers")

    private val dayCollectionRef = Firebase.firestore.collection("days")

    private val salaryCollectionRef = Firebase.firestore.collection("salary")

    private val holidaysCollectionRef = Firebase.firestore.collection("holidays")

    private lateinit var RV: RecyclerView

    private lateinit var wList: ArrayList<Worker>

    private lateinit var dList: ArrayList<Worker>

    private lateinit var msList: ArrayList<MonthSalary>

    private lateinit var hList: ArrayList<Holidays>

    private lateinit var wl: List<Worker>

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

        //fragmentManager?.let { clearBackStack(it) }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack(R.id.workerFragment, false)
        }

        RV = _binding.wSalaryList

        val manager = LinearLayoutManager(activity)

        RV.layoutManager = manager

        RV.setHasFixedSize(true)

        wList = arrayListOf()

        dList = arrayListOf()

        msList = arrayListOf()

        hList = arrayListOf()

        wl = listOf()

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

        setHasOptionsMenu(true)

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

    private fun saveDailySalary(worker: Worker) = CoroutineScope(Dispatchers.IO).launch {

        try {
            dayCollectionRef.add(worker).await()
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

                }

            }
            wl = wList.sortedBy { it.fName }
            val ms = msList.filter { it.month.contains("$month $year") }
            RV.adapter = SRV(this, wl, arrayListOf(*ms.toTypedArray()), "$month $year", false)
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
                //Toast.makeText(requireContext(), "No Worker Matched The Query", Toast.LENGTH_LONG).show()
                saveDailySalary(Worker(worker.fName,worker.lName,worker.dOB,worker.salary,
                    worker.pNumber,worker.country,worker.city,worker.gender,worker.nationality,
                    worker.nationalId,worker.hireDate,worker.aTime,worker.lTime,worker.department,
                    worker.day,dSalary))
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

    fun clearBackStack(fragmentManager: FragmentManager) {
        if (fragmentManager.backStackEntryCount > 1) {
            val entry = fragmentManager.getBackStackEntryAt(1)
            fragmentManager.popBackStack(entry.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

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

    suspend fun calculate (worker: Worker) = CoroutineScope(Dispatchers.IO).launch {
        for (w in dList) {
            if (w.fName == worker.fName && w.lName == worker.lName && w.dOB == worker.dOB &&
                w.day.contains(month)) {
                val hourSalary = w.salary.toInt() / 21 / 8
                val dayHours = w.lTime - w.aTime
                val daySalary = hourSalary  * dayHours
                updateWorkerDailySalary(w, daySalary.toString())
//                dSalary = w.dSalary.toInt()
            }

        }

//        for (w in dList) {
//            if (w.fName == worker.fName && w.lName == worker.lName &&
//                w.dOB == worker.dOB && w.day.contains(month) && w.dSalary != "") {
//                dSalary = w.dSalary.toInt()
//                msSalary += dSalary
//            }
//        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @OptIn(DelicateCoroutinesApi::class)
    override fun onItemClick(position: Int) {

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {

        }

        val employee = wl[position]
//        var monthSalary = 0
        msSalary = 0

        _binding.wSalaryList.visibility = View.GONE
        _binding.loading.visibility = View.VISIBLE

        animator1 = LoadingAnimation(_binding.loading, -360f)

        animator1.duration = 1000
        animator1.repeatCount = 1
        _binding.loading.startAnimation(animator1)


        GlobalScope.launch {
            withContext(Dispatchers.IO){
                calculate(employee)

                delay(1000)
                for (w in dList) {
                if (w.fName == employee.fName && w.lName == employee.lName &&
                    w.dOB == employee.dOB && w.day.contains(month) && w.dSalary != "") {
                    dSalary = w.dSalary.toInt()
                    msSalary += dSalary
                }
            }

            }

            withContext(Dispatchers.Main){
                findNavController().navigate(SalaryFragmentDirections
                .actionSalaryFragmentToSalaryDetailsFragment(employee, "$month $year", msSalary.toString()))

            }

        }

//        if (dList.contains(Worker(employee.fName,employee.lName,employee.dOB,employee.salary))){
//            Toast.makeText(requireContext(), month, Toast.LENGTH_LONG).show()
//        }

//        for (w in dList) {
//            if (w.fName == employee.fName && w.lName == employee.lName && w.dOB == employee.dOB &&
//                    w.day.contains(month)) {
//                val hourSalary = w.salary.toInt() / 21 / 24
//                val dayHours = w.lTime - w.aTime
//                val daySalary = hourSalary  * dayHours
//                updateWorkerDailySalary(w, daySalary.toString())
////                dSalary = w.dSalary.toInt()
//            }
//
//        }

        for (w in msList) {
            if (w.fName == employee.fName && w.lName == employee.lName && w.dOB == employee.dOB &&
                w.month == month && w.daysoff != "" && w.bonus != "" && w.deduction != "") {
                if (w.daysoff.toInt() >= 1) {
                    val hourSalary = employee.salary.toInt() / 21 / 8
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
                val hourSalary = employee.salary.toInt() / 21 / 8
                val daySalary = hourSalary  * 8
                s = 1
                msSalary += (s * daySalary)
            }
        }


//            for (w in dList) {
//                if (w.fName == employee.fName && w.lName == employee.lName &&
//                    w.dOB == employee.dOB && w.day.contains(month) && w.dSalary != "") {
//                    dSalary = w.dSalary.toInt()
//                    msSalary += dSalary
//                }
//            }


//        Timer().schedule(200) {
//            findNavController().navigate(SalaryFragmentDirections
//            .actionSalaryFragmentToSalaryDetailsFragment(employee, month, msSalary.toString()))




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

    fun filterAllYear() {
        //_binding.wSalaryList.isEnabled = false

        val msy = msList.filter { it.month.contains(year.toString()) }

        RV.adapter = SRV(this, wl, arrayListOf(*msy.toTypedArray()), year.toString(), true)

        RV.isFocusable = false

        RV.isClickable = false

        RV.isEnabled = false

        _binding.allEmployeesSalaries.text = ""
    }

    fun filterMonth(mon: String) {
        val msm = msList.filter { it.month.contains("$mon $year") }

        if (month.contains(mon)) {

            RV.adapter = SRV(this, wl, arrayListOf(*msm.toTypedArray()), "$mon $year", false)

            RV.isFocusableInTouchMode = true

            RV.isClickable = true

            RV.isEnabled = true

            _binding.allEmployeesSalaries.text = "Tap on the Employee's Name to Calculate Their Salary "

        } else {

            //_binding.wSalaryList.isEnabled = false

            RV.adapter = SRV(this, wl, arrayListOf(*msm.toTypedArray()), "$mon $year", true)

            RV.isFocusable = false

            RV.isClickable = false

            RV.isEnabled = false

            _binding.allEmployeesSalaries.text = ""
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.filter_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.jan -> filterMonth("January")
            R.id.feb -> filterMonth("February")
            R.id.mar -> filterMonth("March")
            R.id.apr -> filterMonth("April")
            R.id.may -> filterMonth("May")
            R.id.jun -> filterMonth("June")
            R.id.jul -> filterMonth("July")
            R.id.aug -> filterMonth("August")
            R.id.sep -> filterMonth("September")
            R.id.oct -> filterMonth("October")
            R.id.nov -> filterMonth("November")
            R.id.dec -> filterMonth("December")
            R.id.all_year -> filterAllYear()
            else -> return super.onContextItemSelected(item)
        }
        return true
    }

}