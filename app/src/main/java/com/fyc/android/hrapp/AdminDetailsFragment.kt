package com.fyc.android.hrapp

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.fyc.android.hrapp.databinding.FragmentAdminDetailsBinding
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AdminDetailsFragment : Fragment() {

    private lateinit var _binding: FragmentAdminDetailsBinding

    private lateinit var admin: Admin

    private val adminCollectionRef = Firebase.firestore.collection("admins")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_admin_details, container, false)

        admin = arguments?.let { AdminDetailsFragmentArgs.fromBundle(it).admin }!!

        _binding.aEmail.setText(admin.email)
        _binding.aType.setText(admin.type)

        _binding.applyEditFab.setOnClickListener {
            if (_binding.aEmail.text.isNotEmpty() && _binding.aType.text.isNotEmpty()) {
                if (_binding.aType.text.toString() == "main" ||
                    _binding.aType.text.toString() == "add" ||
                    _binding.aType.text.toString() == "edit" ||
                    _binding.aType.text.toString() == "delete"
                ) {
                    updateAdmin(Admin(_binding.aEmail.text.toString(),
                        _binding.aType.text.toString()))
                    findNavController().navigate(R.id.action_adminDetailsFragment_to_adminFragment)
                } else {
                    Toast.makeText(requireContext(),
                        "Admin Type Must Be main or add or edit or delete only",
                        Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(requireContext(),
                    "Please Fill in The Required Information",
                    Toast.LENGTH_LONG).show()
            }
        }

        setHasOptionsMenu(true)

        return _binding.root
    }

    fun updateAdmin(admin: Admin) = CoroutineScope(Dispatchers.IO).launch {
        val workerQuery = adminCollectionRef
            .whereEqualTo("email", admin.email)
            .whereEqualTo("type", admin.type)
            .get()
            .await()
        if (workerQuery.documents.isNotEmpty()){
            for (document in workerQuery){
                try {
                    adminCollectionRef.document(document.id)
                        .update("email", _binding.aEmail.text.toString()).await()
                    adminCollectionRef.document(document.id)
                        .update("type", _binding.aType.text.toString()).await()

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }

        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "No Worker Matched The Query", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun deleteAdmin(admin: Admin) = CoroutineScope(Dispatchers.IO).launch {
        val workerQuery = adminCollectionRef
            .whereEqualTo("email", admin.email)
            .whereEqualTo("type", admin.type)
            .get()
            .await()
        if (workerQuery.documents.isNotEmpty()){
            for (document in workerQuery){
                try {
                    adminCollectionRef.document(document.id).delete().await()

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }

        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "No Worker Matched The Query", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater){

        inflater.inflate(R.menu.delete_menu, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.delete_admin -> {deleteAdmin(admin)
            findNavController().navigate(R.id.action_adminDetailsFragment_to_adminFragment)}
        }
        return true
    }
}