package com.fyc.android.hrapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.fyc.android.hrapp.databinding.FragmentInputBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class InputFragment : Fragment() {

    private val workerCollectionRef = Firebase.firestore.collection("workers")
    private lateinit var _binding: FragmentInputBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_input, container, false)

        _binding.addWButton.setOnClickListener {
            if (_binding.wFName.text.isNotEmpty() && _binding.wLName.text.isNotEmpty() &&
                _binding.dOB.text.isNotEmpty() && _binding.wSalary.text.isNotEmpty() &&
                _binding.phoneNum.text.isNotEmpty()){
                saveWorker(
                    Worker(
                        _binding.wFName.text.toString(),
                        _binding.wLName.text.toString(),
                        _binding.dOB.text.toString(),
                        _binding.wSalary.text.toString(),
                        _binding.phoneNum.text.toString()
                    )
                )
                Toast.makeText(requireContext(),"success", Toast.LENGTH_LONG).show()
            } else{
                Toast.makeText(requireContext(),"Please Add The Required Info", Toast.LENGTH_LONG).show()
            }
        }


        return _binding.root
    }

    private fun saveWorker(worker: Worker) = CoroutineScope(Dispatchers.IO).launch {

        try {
            workerCollectionRef.add(worker).await()
            withContext(Dispatchers.Main) {
//                Toast.makeText(requireContext(),"Successfully Added The Meal",Toast.LENGTH_LONG).show()
            }
        } catch (e:Exception){
            withContext(Dispatchers.Main) {
//                Toast.makeText(requireContext(),e.message,Toast.LENGTH_LONG).show()
            }
        }
    }

}