package com.fyc.android.hrapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fyc.android.hrapp.databinding.FragmentAdminBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class AdminFragment : Fragment(), AdminRV.onClickListener {

    private lateinit var _binding: FragmentAdminBinding

    private lateinit var aList: ArrayList<Admin>

    private lateinit var RV: RecyclerView

    private lateinit var user: String

    private lateinit var admin: List<Admin>

    private val adminCollectionRef = Firebase.firestore.collection("admins")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_admin, container, false)

        aList = arrayListOf()

        RV = _binding.adminsList

        val manager = LinearLayoutManager(activity)

        RV.layoutManager = manager

        RV.setHasFixedSize(true)

        getLiveUpdates()

        admin = listOf()

        user = Firebase.auth.currentUser?.email ?: ""

        _binding.addFab.setOnClickListener {
            admin = aList.filter { it.email == user }
            for (a in admin) {
                if (a.type == "main") {
                    findNavController().navigate(R.id.action_adminFragment_to_adminInputFragment)
                } else {
                    Toast.makeText(requireContext(), "Access Denied", Toast.LENGTH_LONG).show()
                }
            }

        }

        return _binding.root
    }

    private fun getLiveUpdates(){

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
                    RV.adapter = AdminRV(this, aList.sortedBy { it.email })
                }

            }
        }
    }

    override fun onItemClick(position: Int) {
        //Toast.makeText(requireContext(), aList[position].type, Toast.LENGTH_LONG).show()
        admin = aList.filter { it.email == user }
        for (a in admin) {
            if (a.type == "main") {
                findNavController().navigate(AdminFragmentDirections
                    .actionAdminFragmentToAdminDetailsFragment(aList[position]))
            } else {
                Toast.makeText(requireContext(), "Access Denied", Toast.LENGTH_LONG).show()
            }
        }

    }

}