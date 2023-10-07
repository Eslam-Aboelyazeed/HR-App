package com.fyc.android.hrapp

import android.annotation.SuppressLint
import android.app.TimePickerDialog
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
import com.fyc.android.hrapp.databinding.FragmentADetailsBinding
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class ADetailsFragment : Fragment() {

    private lateinit var _binding: FragmentADetailsBinding

    private lateinit var worker: Worker

    private lateinit var day: String

    private lateinit var wList: ArrayList<Worker>

    private val dayCollectionRef = Firebase.firestore.collection("days")

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n", "SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_a_details, container, false)

        _binding.wATime.isFocusable = false
        _binding.wATimeMin.isFocusable = false
        _binding.wLTime.isFocusable = false
        _binding.wLTimeMin.isFocusable = false

        worker = arguments?.let { ADetailsFragmentArgs.fromBundle(it).worker }!!

        day = arguments.let { ADetailsFragmentArgs.fromBundle(it!!).day }

        _binding.wName.text = worker.fName + " " + worker.lName

        wList = arrayListOf()

        getLiveUpdatesForWorkers()

//        for (w in wList) {
//            if (w.fName == worker.fName && w.lName == worker.lName &&
//                w.nationalId == worker.nationalId && w.day == day)
//            _binding.wATime.setText(w.aTime)
//            _binding.wATimeMin.setText(w.aTimemin)
//            _binding.wLTime.setText(w.lTime)
//            _binding.wLTimeMin.setText(w.lTimemin)
//        }

        val aTimePickerDialog = TimePickerDialog(
            requireContext(),
            R.style.Theme_AppCompat_DayNight_Dialog_Alert,
            { view, hourOfDay, minute ->
                // Handle the selected time.
                _binding.wATime.setText(hourOfDay.toString())
                _binding.wATimeMin.setText(minute.toString())
            },
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY), // Initial hour of the day.
            Calendar.getInstance().get(Calendar.MINUTE), // Initial minute of the hour.
            true // Whether to use the 24-hour format.
        )

        _binding.aTimePicker.setOnClickListener {
            aTimePickerDialog.show()
        }

        val lTimePickerDialog = TimePickerDialog(
            requireContext(),
            R.style.Theme_AppCompat_DayNight_Dialog_Alert,
            { view, hourOfDay, minute ->
                // Handle the selected time.
                _binding.wLTime.setText(hourOfDay.toString())
                _binding.wLTimeMin.setText(minute.toString())
            },
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY), // Initial hour of the day.
            Calendar.getInstance().get(Calendar.MINUTE), // Initial minute of the hour.
            true // Whether to use the 24-hour format.
        )

        _binding.lTimePicker.setOnClickListener {
            lTimePickerDialog.show()
        }


        _binding.applyEditFab.setOnClickListener {

            if (_binding.wATime.text.isNotEmpty() && _binding.wATimeMin.text.isNotEmpty() &&
                _binding.wLTime.text.isNotEmpty() && _binding.wLTimeMin.text.isNotEmpty()) {
                if (_binding.wATime.text.toString().toInt() < _binding.wLTime.text.toString().toInt()){
                    updateWorkerAttendance(getEditedWorkerAttendance())

                    findNavController().navigate(ADetailsFragmentDirections.actionADetailsFragmentToAttendanceFragment(day, false))
                } else {
                    Toast.makeText(
                        requireContext(), "Please Input the Time Using the 24 Hours Format" , Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(
                    requireContext(), "Please Input All the Information" , Toast.LENGTH_LONG).show()
            }

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

    @RequiresApi(Build.VERSION_CODES.O)
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
//                    val localDateTime1 = LocalTime.parse(_binding.wATime.text.toString())
//                    val localDateTime2 = LocalTime.parse(_binding.wLTime.text.toString())
//                    val formatter = DateTimeFormatter.ofPattern("HH.MM")
//                    val output1 = formatter.format(localDateTime1)
//                    val output2 = formatter.format(localDateTime2)

                    if (_binding.wATimeMin.text.toString() == "0" || _binding.wATimeMin.text.toString() == "00"){
                        dayCollectionRef.document(document.id).update("atimemin","00")
                    }else {
                        dayCollectionRef.document(document.id).update("atimemin",_binding.wATimeMin.text.toString().toInt().toString())
                    }

                    if (_binding.wLTimeMin.text.toString() == "0" || _binding.wLTimeMin.text.toString() == "00"){
                        dayCollectionRef.document(document.id).update("ltimemin","00")
                    }else {
                        dayCollectionRef.document(document.id).update("ltimemin",_binding.wLTimeMin.text.toString().toInt().toString())
                    }

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

    private fun getLiveUpdatesForWorkers() {

        dayCollectionRef.addSnapshotListener{querySnapshot, firebaseFirestoreException ->

            wList.clear()
            firebaseFirestoreException?.let {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }

            querySnapshot?.let {
                for (document in it){
                    val wAttendance = document.toObject<Worker>()
                    if (wAttendance.day == day) {
                        wList.add(wAttendance)
                    }
                }
            }
            for (w in wList) {
                if (w.fName == worker.fName && w.lName == worker.lName &&
                    w.nationalId == worker.nationalId && w.day == day) {
                    _binding.wATime.setText(w.aTime.toString())
                    _binding.wATimeMin.setText(w.aTimemin)
                    _binding.wLTime.setText(w.lTime.toString())
                    _binding.wLTimeMin.setText(w.lTimemin)
                }
            }
        }
    }
}