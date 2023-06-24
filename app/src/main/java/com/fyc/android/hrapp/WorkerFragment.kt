package com.fyc.android.hrapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fyc.android.hrapp.databinding.FragmentWorkerBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase


class WorkerFragment : Fragment(), WRV.onClickListener {

    private lateinit var RV: RecyclerView

    private lateinit var wList: Workers

    private lateinit var _binding: FragmentWorkerBinding

    private val workerCollectionRef = Firebase.firestore.collection("workers")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_worker, container, false)

        RV = _binding.workersList

        val manager = LinearLayoutManager(activity)

        RV.layoutManager = manager

        RV.setHasFixedSize(true)

        wList = Workers()

        _binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_workerFragment_to_inputFragment)
        }

        _binding.cFab.setOnClickListener {
            findNavController().navigate(R.id.action_workerFragment_to_calendarFragment)
        }

        getLiveUpdates()


        return _binding.root
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
                    wList.add(worker)
                    RV.adapter = WRV(this, wList)
                }

            }
        }
    }

    override fun onItemClick(position: Int) {
        findNavController().navigate(WorkerFragmentDirections.actionWorkerFragmentToWDetailsFragment(wList[position]))
    }

}