package com.fyc.android.hrapp

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
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class AttendanceFragment : Fragment(), WRV.onClickListener, ARV.onClickListener {

    private lateinit var RV: RecyclerView

    private lateinit var _binding: FragmentAttendanceBinding

    private lateinit var allWList: ArrayList<Worker>

    private lateinit var attendedWList: Workers

    private lateinit var wList: ArrayList<Worker>

    private lateinit var day: String

    private val workerCollectionRef = Firebase.firestore.collection("workers")

    private val dayCollectionRef = Firebase.firestore.collection("days")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_attendance, container, false)

        day = arguments.let { AttendanceFragmentArgs.fromBundle(it!!).theDay }

        RV = _binding.allWorkersList

        val manager = LinearLayoutManager(activity)

        RV.layoutManager = manager

        RV.setHasFixedSize(true)

        allWList = arrayListOf()

//        attendedWList = Workers()

        wList = arrayListOf()

        wList.clear()

        setHasOptionsMenu(true)

        getLiveUpdates()

        getLiveUpdatesForWorkers()

//        RV.adapter = WRV(this, attendedWList)

        _binding.applyEditFab.setOnClickListener {
            _binding.applyEditFab.isClickable = false
            _binding.applyEditFab.visibility = View.INVISIBLE

            _binding.addedFab.visibility = View.VISIBLE
            _binding.addedFab.isClickable = true

//            saveAttendance(WAttendance(day, wList))

            updateWorkerAttendance(getEditedWorkerAttendance())

            getLiveUpdatesForWorkers()

//            RV.adapter = WRV(this, attendedWList)
        }

        _binding.addedFab.setOnClickListener {
//            val map: Map<String, ArrayList<Worker>>
//            map = mutableMapOf()
//            map[day] = attendedWList
//            saveAttendance(WAttendance(day, wList))
            findNavController().navigate(R.id.action_attendanceFragment_to_calendarFragment)
        }


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

    fun getEditedWorkerAttendance(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        if(day.isNotEmpty()) {
            map["day"] = day
        }
        if (wList.isNotEmpty()) {
            map["workers"] = wList
        }

        return map
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
                saveAttendance(WAttendance(day, wList))
            }
        }
    }

    fun deleteWorkerAttendance() = CoroutineScope(Dispatchers.IO).launch {
        val workerQuery = dayCollectionRef
            .whereEqualTo("day", day)
            .get()
            .await()
        if (workerQuery.documents.isNotEmpty()){
            for (document in workerQuery){
                try {
                    dayCollectionRef.document(document.id).delete().await()
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

    private fun getLiveUpdates(){

        workerCollectionRef.addSnapshotListener{querySnapshot, firebaseFirestoreException ->

            allWList.clear()
            firebaseFirestoreException?.let {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }

            querySnapshot?.let {
                for (document in it){
                    val worker = document.toObject<Worker>()
                    allWList.add(worker)
                    //RV.adapter = WRV(this, wList)
                }

            }
        }
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
                    val wAttendance = document.toObject<WAttendance>()
                    if (wAttendance.day == day) {
                        wList = wAttendance.workers
                        RV.adapter = WRV(this, wList)
                    }

                }

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater){

        inflater.inflate(R.menu.overflow_menu, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.delete_worker -> { deleteWorkerAttendance(); getLiveUpdatesForWorkers()}
            R.id.edit_worker -> { RV.adapter = ARV(this, allWList, wList)
                                  _binding.applyEditFab.isClickable = true;
                                  _binding.applyEditFab.visibility = View.VISIBLE;
                                  _binding.addedFab.isClickable = false;
                                  _binding.addedFab.visibility = View.INVISIBLE}
        }

        return true
    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }

}