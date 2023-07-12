package com.fyc.android.hrapp

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.fyc.android.hrapp.databinding.FragmentSalaryDetailsBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SalaryDetailsFragment : Fragment() {

    private lateinit var _binding: FragmentSalaryDetailsBinding

    private lateinit var worker: Worker

    private lateinit var month: String

    private lateinit var mSalary: String

    private lateinit var user: String

    private lateinit var aList: ArrayList<Admin>

    private lateinit var admin: List<Admin>

    private val adminCollectionRef = Firebase.firestore.collection("admins")

    private val workerCollectionRef = Firebase.firestore.collection("workers")

    private val dayCollectionRef = Firebase.firestore.collection("days")

    private val salaryCollectionRef = Firebase.firestore.collection("salary")

    private val holidaysCollectionRef = Firebase.firestore.collection("holidays")

    private lateinit var msList: ArrayList<MonthSalary>

    private lateinit var hList: ArrayList<Holidays>

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_salary_details, container, false)

        worker = arguments?.let { SalaryDetailsFragmentArgs.fromBundle(it).sWorker }!!

        month = arguments.let { SalaryDetailsFragmentArgs.fromBundle(it!!).month }

        mSalary = arguments.let { SalaryDetailsFragmentArgs.fromBundle(it!!).mSalary }

        msList = arrayListOf()

        hList = arrayListOf()

        getMonthLiveUpdates()

        _binding.wMSalary.isFocusable = false
        _binding.wMDaysOff.isFocusable = false
        _binding.wMBonus.isFocusable = false
        _binding.wMDeduction.isFocusable = false

        _binding.wMSalary.setText(mSalary)

        _binding.wName.text = worker.fName + " " + worker.lName

        _binding.month.text = month

//        for (ms in msList) {
//            if (ms.fName == worker.fName && ms.lName == worker.lName && ms.dOB == worker.dOB &&
//                    ms.month == month) {
//                _binding.wMDaysOff.setText(ms.daysoff)
//                _binding.wMBonus.setText(ms.bonus)
//                _binding.wMDeduction.setText(ms.deduction)
//            }
//        }

        aList = arrayListOf()

        admin = listOf()

        user = Firebase.auth.currentUser?.email ?: ""

        getAdminsLiveUpdates()


        setHasOptionsMenu(true)

        for (a in admin) {
            if (a.type == "main" || a.type == "add" || a.type == "edit") {
                updateWorkerMonthlySalary()
            } else {
                Toast.makeText(requireContext(), "Access Denied", Toast.LENGTH_LONG).show()
            }
        }

        _binding.eDoneFab.setOnClickListener {
            if (//_binding.wMSalary.text.toString().isNotEmpty() &&
                _binding.wMDaysOff.text.toString().isNotEmpty() &&
                _binding.wMBonus.text.toString().isNotEmpty() &&
                _binding.wMDeduction.text.toString().isNotEmpty()) {
                if (_binding.wMDaysOff.text.toString().toInt() >= 1) {
                    val hourSalary = worker.salary.toInt() / 21 / 24
                    val daySalary = hourSalary  * 8
                    val s = mSalary.toInt() + daySalary + _binding.wMBonus.text.toString().toInt() -
                            _binding.wMDeduction.text.toString().toInt()
                    updateWorkerMonthlySalaryManually(s.toString(),
                        _binding.wMDaysOff.text.toString(),_binding.wMBonus.text.toString(),
                        _binding.wMDeduction.text.toString())
                    getMonthLiveUpdates()
                    _binding.wMDaysOff.isFocusable = false
                    _binding.wMBonus.isFocusable = false
                    _binding.wMDeduction.isFocusable = false
                    _binding.eDoneFab.isClickable = false
                    _binding.eDoneFab.visibility = View.INVISIBLE
                } else {
                    val s = mSalary.toInt() + _binding.wMBonus.text.toString().toInt() -
                            _binding.wMDeduction.text.toString().toInt()
                    updateWorkerMonthlySalaryManually(s.toString(),
                        _binding.wMDaysOff.text.toString(),_binding.wMBonus.text.toString(),
                        _binding.wMDeduction.text.toString())
                    getMonthLiveUpdates()
                    _binding.wMDaysOff.isFocusable = false
                    _binding.wMBonus.isFocusable = false
                    _binding.wMDeduction.isFocusable = false
                    _binding.eDoneFab.isClickable = false
                    _binding.eDoneFab.visibility = View.INVISIBLE
                }
            } else {
                Toast.makeText(
                    requireContext(), "Please Input the Details", Toast.LENGTH_LONG).show()
            }
        }


        return _binding.root
    }

    private fun getAdminsLiveUpdates(){

        adminCollectionRef.addSnapshotListener{querySnapshot, firebaseFirestoreException ->

            aList.clear()
            firebaseFirestoreException?.let {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }

            querySnapshot?.let {
                for (document in it){
                    val admin = document.toObject<Admin>()
                    aList.add(admin)
                }
            }
            admin = aList.filter { it.email == user }
        }
    }

    private fun saveMonthlySalary(monthSalary: MonthSalary) = CoroutineScope(Dispatchers.IO).launch {

        try {
            salaryCollectionRef.add(monthSalary).await()
            withContext(Dispatchers.Main) {
//                Toast.makeText(requireContext(),"Successfully Added The Meal",Toast.LENGTH_LONG).show()
            }
        } catch (e:Exception){
            withContext(Dispatchers.Main) {
//                Toast.makeText(requireContext(),e.message,Toast.LENGTH_LONG).show()
            }
        }
    }

    fun updateWorkerMonthlySalary() = CoroutineScope(Dispatchers.IO).launch {
        val workerQuery = salaryCollectionRef
            .whereEqualTo("fname", worker.fName)
            .whereEqualTo("lname", worker.lName)
            .whereEqualTo("dob", worker.dOB)
            .whereEqualTo("month", month)
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
//                Toast.makeText(requireContext(), "No Worker Matched The Query", Toast.LENGTH_LONG).show()
                saveMonthlySalary(MonthSalary(worker.fName, worker.lName, worker.dOB, month, mSalary))
            }
        }
    }

    fun updateWorkerMonthlySalaryManually(salary: String, daysOff: String, bonus: String, deduction: String) = CoroutineScope(Dispatchers.IO).launch {
        val workerQuery = salaryCollectionRef
            .whereEqualTo("fname", worker.fName)
            .whereEqualTo("lname", worker.lName)
            .whereEqualTo("dob", worker.dOB)
            .whereEqualTo("month", month)
            .get()
            .await()
        if (workerQuery.documents.isNotEmpty()){
            for (document in workerQuery){
                try {
                    salaryCollectionRef.document(document.id).update("salary", salary)
                    salaryCollectionRef.document(document.id).update("daysoff", daysOff)
                    salaryCollectionRef.document(document.id).update("bonus", bonus)
                    salaryCollectionRef.document(document.id).update("deduction", deduction)

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
//                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }

        } else {
            withContext(Dispatchers.Main) {
//                Toast.makeText(requireContext(), "No Worker Matched The Query", Toast.LENGTH_LONG).show()
                saveMonthlySalary(MonthSalary(worker.fName, worker.lName, worker.dOB, month, mSalary))
            }
        }
    }

    fun deleteWorker(worker: Worker) = CoroutineScope(Dispatchers.IO).launch {
        val workerQuery = salaryCollectionRef
            .whereEqualTo("fname", worker.fName)
            .whereEqualTo("lname", worker.lName)
            .whereEqualTo("dob", worker.dOB)
            .whereEqualTo("month", month)
            .get()
            .await()
        if (workerQuery.documents.isNotEmpty()){
            for (document in workerQuery){
                try {
                    salaryCollectionRef.document(document.id).delete().await()
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        //Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }

        } else {
            withContext(Dispatchers.Main) {
                //Toast.makeText(requireContext(), "No Worker Matched The Query", Toast.LENGTH_LONG).show()
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
                for (ms in msList) {
                    if (ms.fName == worker.fName && ms.lName == worker.lName &&
                        ms.dOB == worker.dOB && ms.month == month) {
                        _binding.wMSalary.setText(ms.salary)
                        _binding.wMDaysOff.setText(ms.daysoff)
                        _binding.wMBonus.setText(ms.bonus)
                        _binding.wMDeduction.setText(ms.deduction)
                    }
                }

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater){

        inflater.inflate(R.menu.overflow_menu, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.delete_worker -> {for (a in admin) {
                if (a.type == "main" || a.type == "delete") {
                    deleteWorker(worker);
                    findNavController().navigate(R.id.action_salaryDetailsFragment_to_salaryFragment)
                } else {
                    Toast.makeText(requireContext(), "Access Denied", Toast.LENGTH_LONG).show()
                }
            }
            }
            R.id.edit_worker -> {for (a in admin) {
                if (a.type == "main" || a.type == "edit") {
                    _binding.wMDaysOff.isFocusableInTouchMode = true;
                    _binding.wMBonus.isFocusableInTouchMode = true;
                    _binding.wMDeduction.isFocusableInTouchMode = true;
                    _binding.eDoneFab.isClickable = true;
                    _binding.eDoneFab.visibility = View.VISIBLE
                } else {
                    Toast.makeText(requireContext(), "Access Denied", Toast.LENGTH_LONG).show()
                }
            }
            }
        }

        return true
    }

}