package com.fyc.android.hrapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.fyc.android.hrapp.databinding.FragmentAdminInputBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AdminInputFragment : Fragment() {

    private lateinit var _binding: FragmentAdminInputBinding

    private lateinit var user: String

    private lateinit var aList: ArrayList<Admin>

    private lateinit var admin: List<Admin>

    private val adminCollectionRef = Firebase.firestore.collection("admins")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_admin_input, container, false)

        _binding.AddFab.setOnClickListener {

            if (_binding.adminEmail.text.isNotEmpty()) {

                if (_binding.radioGroup.checkedRadioButtonId == -1) {

                    Toast.makeText(
                        requireContext(), "Please Choose The Admin Type", Toast.LENGTH_LONG
                    ).show()
                }

                if (_binding.radioGroup.checkedRadioButtonId == R.id.main) {

                    saveAdmin(Admin(_binding.adminEmail.text.toString(), "main"))
                    findNavController().navigate(R.id.action_adminInputFragment_to_adminFragment)
                }

                if (_binding.radioGroup.checkedRadioButtonId == R.id.add) {

                    saveAdmin(Admin(_binding.adminEmail.text.toString(), "add"))
                    findNavController().navigate(R.id.action_adminInputFragment_to_adminFragment)
                }

                if (_binding.radioGroup.checkedRadioButtonId == R.id.edit) {

                    saveAdmin(Admin(_binding.adminEmail.text.toString(), "edit"))
                    findNavController().navigate(R.id.action_adminInputFragment_to_adminFragment)
                }

                if (_binding.radioGroup.checkedRadioButtonId == R.id.delete) {

                    saveAdmin(Admin(_binding.adminEmail.text.toString(), "delete"))
                    findNavController().navigate(R.id.action_adminInputFragment_to_adminFragment)
                }
            } else {
                Toast.makeText(
                    requireContext(), "Please Input The Admin Email", Toast.LENGTH_LONG
                ).show()
            }
        }

        return _binding.root
    }

    private fun saveAdmin(admin: Admin) = CoroutineScope(Dispatchers.IO).launch {

        try {
            adminCollectionRef.add(admin).await()
            withContext(Dispatchers.Main) {
//                Toast.makeText(requireContext(),"Successfully Added The Meal",Toast.LENGTH_LONG).show()
            }
        } catch (e:Exception){
            withContext(Dispatchers.Main) {
//                Toast.makeText(requireContext(),e.message,Toast.LENGTH_LONG).show()
            }
        }
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
}