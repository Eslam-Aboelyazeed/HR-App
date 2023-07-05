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
import com.google.firebase.firestore.ktx.firestore
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

    private val workerCollectionRef = Firebase.firestore.collection("workers")

    private val dayCollectionRef = Firebase.firestore.collection("days")

    private val salaryCollectionRef = Firebase.firestore.collection("salary")

    private val holidaysCollectionRef = Firebase.firestore.collection("holidays")

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

        _binding.wMSalary.isFocusable = false

        _binding.wMSalary.setText(mSalary)

        _binding.wName.text = worker.fName + " " + worker.lName

        _binding.month.text = month

        setHasOptionsMenu(true)

        updateWorkerMonthlySalary()

        _binding.eDoneFab.setOnClickListener {
            if (_binding.wMSalary.text.toString().isNotEmpty()){
                updateWorkerMonthlySalaryManually(_binding.wMSalary.text.toString())
            } else {
                Toast.makeText(requireContext(), "Please Input a Salary", Toast.LENGTH_LONG).show()
            }
            _binding.wMSalary.isFocusable = false
            _binding.eDoneFab.isClickable = false
            _binding.eDoneFab.visibility = View.INVISIBLE
        }


        return _binding.root
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

    fun updateWorkerMonthlySalaryManually(salary: String) = CoroutineScope(Dispatchers.IO).launch {
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater){

        inflater.inflate(R.menu.overflow_menu, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.delete_worker -> {deleteWorker(worker);
                findNavController().navigate(R.id.action_salaryDetailsFragment_to_salaryFragment)}
            R.id.edit_worker -> { _binding.wMSalary.isFocusableInTouchMode = true;
                _binding.eDoneFab.isClickable = true;
                _binding.eDoneFab.visibility = View.VISIBLE}
        }

        return true
    }

}