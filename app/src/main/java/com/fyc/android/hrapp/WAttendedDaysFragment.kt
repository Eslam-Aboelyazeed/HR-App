package com.fyc.android.hrapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fyc.android.hrapp.databinding.FragmentWAttendedDaysBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class WAttendedDaysFragment : Fragment() {

    private lateinit var _binding: FragmentWAttendedDaysBinding

    private val dayCollectionRef = Firebase.firestore.collection("days")

    private lateinit var emList: ArrayList<Worker>

    private lateinit var eList: List<Worker>

    private lateinit var employee: Worker

    private lateinit var RV: RecyclerView

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

        RV = _binding.wAttendedDaysList

        val manager = LinearLayoutManager(activity)

        RV.layoutManager = manager

        RV.setHasFixedSize(true)

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
            eList = emList.filter { it.nationalId == employee.nationalId }
            eList.sortedBy { it.day }
            RV.adapter = ADRV(eList)
        }
    }

}