package com.fyc.android.hrapp

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fyc.android.hrapp.databinding.FragmentAttendanceBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class AttendanceFragment : Fragment(), ARV.onClickListener {

    private lateinit var RV: RecyclerView

    private lateinit var _binding: FragmentAttendanceBinding

    private lateinit var allWList: ArrayList<Worker>

//    private lateinit var attendedWList: AttendedWorkers

    private lateinit var wList: ArrayList<Worker>

    private lateinit var day: String

    private lateinit var user: String

    private lateinit var aList: ArrayList<Admin>

    private lateinit var admin: List<Admin>

    private val adminCollectionRef = Firebase.firestore.collection("admins")

    private val workerCollectionRef = Firebase.firestore.collection("workers")

    private val dayCollectionRef = Firebase.firestore.collection("days")

    private val holidaysCollectionRef = Firebase.firestore.collection("holidays")

    private lateinit var hList: ArrayList<Holidays>

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_attendance, container, false)

        day = arguments.let { AttendanceFragmentArgs.fromBundle(it!!).theDay }

        val bool = arguments.let { AttendanceFragmentArgs.fromBundle(it!!).bool }

        RV = _binding.allWorkersList

        val manager = LinearLayoutManager(activity)

        RV.layoutManager = manager

        RV.setHasFixedSize(true)

        allWList = arrayListOf()

//        attendedWList = AttendedWorkers()

        wList = arrayListOf()

        hList = arrayListOf()

//        wList.clear()

//        setHasOptionsMenu(true)


        if (bool) {
            _binding.textView3.visibility = View.VISIBLE
            _binding.allWorkersList.visibility = View.GONE

        } else {
            getLiveUpdatesForWorkers()
        }


        aList = arrayListOf()

        admin = listOf()

        user = Firebase.auth.currentUser?.email ?: ""

        getAdminsLiveUpdates()


//        getLiveUpdates()


//        RV.adapter = ARV(this, allWList, wList)


//        _binding.applyEditFab.setOnClickListener {
//            _binding.applyEditFab.isClickable = false
//            _binding.applyEditFab.visibility = View.INVISIBLE
//
//            _binding.addedFab.visibility = View.VISIBLE
//            _binding.addedFab.isClickable = true
//
////            saveAttendance(WAttendance(day, wList))
//
//            updateWorkerAttendance(getEditedWorkerAttendance())
//
//            RV.adapter = ARV(this, attendedWList, allWList)
//
////            getLiveUpdatesForWorkers()
//
////            RV.adapter = WRV(this, attendedWList)
//        }

//        _binding.addedFab.setOnClickListener {
//            val map: Map<String, ArrayList<Worker>>
//            map = mutableMapOf()
//            map[day] = attendedWList
//            saveAttendance(WAttendance(day, wList))
//            findNavController().navigate(R.id.action_attendanceFragment_to_calendarFragment)
//        }


        return _binding.root
    }

    private fun saveAttendance(wAttendance: WAttendance) = CoroutineScope(Dispatchers.IO).launch {

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

//    fun getEditedWorkerAttendance(): Map<String, Any> {
//        val map = mutableMapOf<String, Any>()
//        if(day.isNotEmpty()) {
//            map["day"] = day
//        }
//        if (attendedWList.isNotEmpty()) {
//            map["workers"] = attendedWList
//        }
//
//        return map
//    }

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

    fun updateWorkerAttendance(newWorkerMap: Map<String, Any>) = CoroutineScope(Dispatchers.IO).launch {
        val workerQuery = dayCollectionRef
            .whereEqualTo("day", day)
            .get()
            .await()
        if (workerQuery.documents.isNotEmpty()){
            for (document in workerQuery){
                try {
                    dayCollectionRef.document(document.id).set(
                        newWorkerMap, SetOptions.merge()
                    ).await()

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
//                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }

        } else {
            withContext(Dispatchers.Main) {
//                Toast.makeText(requireContext(), "No Worker Matched The Query", Toast.LENGTH_LONG).show()
//                saveAttendance(WAttendance(day, attendedWList))
            }
        }
    }

//    fun deleteWorkerAttendance() = CoroutineScope(Dispatchers.IO).launch {
//        val workerQuery = dayCollectionRef
//            .whereEqualTo("day", day)
//            .get()
//            .await()
//        if (workerQuery.documents.isNotEmpty()){
//            for (document in workerQuery){
//                try {
//                    dayCollectionRef.document(document.id).delete().await()
//                } catch (e: Exception) {
//                    withContext(Dispatchers.Main) {
//                        //Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
//                    }
//                }
//            }
//
//        } else {
//            withContext(Dispatchers.Main) {
//                //Toast.makeText(requireContext(), "No Worker Matched The Query", Toast.LENGTH_LONG).show()
//            }
//        }
//    }

    private fun getLiveUpdates(){

        workerCollectionRef.addSnapshotListener{querySnapshot, firebaseFirestoreException ->

            allWList.clear()
//            attendedWList.clear()
            firebaseFirestoreException?.let {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }

            querySnapshot?.let {
                for (document in it){
                    val worker = document.toObject<Worker>()
                    allWList.add(worker)
                    RV.adapter = ARV(this, allWList.sortedBy { it.fName }, wList)
                }

            }
        }
//        for (worker in allWList) {
//            attendedWList.add(AttendedWorker(worker.fName,worker.lName,worker.dOB,worker.salary,
//                worker.pNumber,worker.country,worker.city,worker.gender,worker.nationality,
//                worker.nationalId,worker.hireDate,0,0,worker.department))
//        }
//        for (worker in allWList) {
//            saveAttendance(WAttendance(day, worker))
//        }
    }

    private fun getLiveUpdatesForWorkers(){

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
//                        RV.adapter = WRV(this, wList)
                    }
//                    do {
//                        wList.add(wAttendance)
//                    } while (wAttendance.day == day)

                }
                getLiveUpdates()

            }
        }
    }

    private fun getHolidaysLiveUpdates(){

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

    override fun onItemClick(position: Int) {
        for (a in admin) {
            if (a.type == "main" || a.type == "edit" || a.type == "add") {
                findNavController().navigate(AttendanceFragmentDirections.
                actionAttendanceFragmentToADetailsFragment(allWList[position],day))
            } else {
                Toast.makeText(requireContext(), "Access Denied", Toast.LENGTH_LONG).show()
            }
        }

    }

}