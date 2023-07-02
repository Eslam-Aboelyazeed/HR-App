package com.fyc.android.hrapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.fyc.android.hrapp.databinding.FragmentADetailsBinding
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ADetailsFragment : Fragment() {

    private lateinit var _binding: FragmentADetailsBinding

    private lateinit var worker: Worker

    private lateinit var day: String

    private val dayCollectionRef = Firebase.firestore.collection("days")

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_a_details, container, false)

        worker = arguments?.let { ADetailsFragmentArgs.fromBundle(it).worker }!!


        day = arguments.let { ADetailsFragmentArgs.fromBundle(it!!).day }

        _binding.wName.text = worker.fName + " " + worker.lName

        _binding.applyEditFab.setOnClickListener {

            updateWorkerAttendance(getEditedWorkerAttendance())

            findNavController().navigate(ADetailsFragmentDirections.actionADetailsFragmentToAttendanceFragment(day))

        }

        return _binding.root
    }

    private fun saveAttendance(wAttendance: Worker) = CoroutineScope(Dispatchers.IO).launch {

        try {
            dayCollectionRef.add(wAttendance).await()
            withContext(Dispatchers.Main) {
//                Toast.makeText(requireContext(),"Successfully Added The Meal",Toast.LENGTH_LONG).show()
            }
        } catch (e:Exception){
            withContext(Dispatchers.Main) {
//                Toast.makeText(requireContext(),e.message,Toast.LENGTH_LONG).show()
            }
        }
    }

    fun getEditedWorkerAttendance(): Map<String, Any> {
        val aTime = _binding.wATime.text.toString().toInt()
        val lTime = _binding.wLTime.text.toString().toInt()
        val map = mutableMapOf<String, Any>()
        if(aTime.toString().isNotEmpty()) {
            map["workers"]= mapOf(Pair("atime",aTime))
        }
        if (lTime.toString().isNotEmpty()) {
            map["workers"] = mapOf(Pair("ltime",lTime))
        }

        return map
    }

    fun updateWorkerAttendance(newWorkerMap: Map<String, Any>) = CoroutineScope(Dispatchers.IO).launch {
        val workerQuery = dayCollectionRef
            .whereEqualTo("day", day)
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
                    dayCollectionRef.document(document.id).update("atime",_binding.wATime.text.toString().toInt())
                    dayCollectionRef.document(document.id).update("ltime",_binding.wLTime.text.toString().toInt())
//                    dayCollectionRef.document(document.id).set(
//                        newWorkerMap, SetOptions.merge()
//                    ).await()

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
//                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }

        } else {
            withContext(Dispatchers.Main) {
//                Toast.makeText(requireContext(), "No Worker Matched The Query", Toast.LENGTH_LONG).show()
                saveAttendance(
                    Worker(worker.fName,worker.lName,worker.dOB,
                    worker.salary,worker.pNumber,worker.country,worker.city,worker.gender,
                worker.nationality,worker.nationalId,worker.hireDate,
                    _binding.wATime.text.toString().toInt(),_binding.wLTime.text.toString().toInt(),
                worker.department,day)
                    )
            }
        }
    }
}