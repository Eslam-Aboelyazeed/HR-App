package com.fyc.android.hrapp

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import com.fyc.android.hrapp.databinding.FragmentWorkerBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase


class WorkerFragment : Fragment(), WRV.onClickListener {

    private val viewModel by activityViewModels<LoginViewModel>()

    private lateinit var RV: RecyclerView

    private lateinit var wList: Workers

    private lateinit var _binding: FragmentWorkerBinding

    private lateinit var user: String

    private lateinit var aList: ArrayList<Admin>

    private lateinit var admin: List<Admin>

    private val adminCollectionRef = Firebase.firestore.collection("admins")

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

        aList = arrayListOf()

        admin = listOf()

        getAdminsLiveUpdates()

        _binding.fab.setOnClickListener {
            admin = aList.filter { it.email == user }
            for (a in admin) {
                if (a.type == "main" || a.type == "add") {
                    findNavController().navigate(R.id.action_workerFragment_to_inputFragment)
                } else {
                    Toast.makeText(requireContext(), "Access Denied", Toast.LENGTH_LONG).show()
                }
            }

        }

//        _binding.cFab.setOnClickListener {
//            findNavController().navigate(R.id.action_workerFragment_to_calendarFragment)
//        }
//
//        _binding.sfab.setOnClickListener {
//            findNavController().navigate(R.id.action_workerFragment_to_salaryFragment)
//        }

        getLiveUpdates()

        setHasOptionsMenu(true)

        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->

            when (authenticationState) {
                LoginViewModel.AuthenticationState.AUTHENTICATED ->{ Log.e(
                    "ReminderListFragment",
                    "Authentication state is $authenticationState");
                    user = Firebase.auth.currentUser?.email ?: "";
                    //Toast.makeText(requireContext(), user, Toast.LENGTH_LONG).show()
                }

                else -> {
                    Log.e(
                    "ReminderListFragment",
                    "Authentication state is $authenticationState")
                    ;findNavController().navigate(R.id.action_workerFragment_to_loginFragment)}
            }
        })

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
        }
    }

    fun confirmAction(message: String, onConfirm: () -> Unit) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(message)
        builder.setCancelable(false)
        builder.setPositiveButton("Yes") { dialog, id ->
            onConfirm()
        }
        builder.setNegativeButton("No") { dialog, id ->
            dialog.dismiss()
        }
        val alert = builder.create()
        alert.show()
    }

    override fun onItemClick(position: Int) {
//        admin = aList.filter { it.email == user }
//        for (a in admin) {
//            if (a.type == "main" || a.type == "edit" || a.type == "delete") {
//                findNavController().navigate(R.id.action_workerFragment_to_inputFragment)
//            } else {
//                Toast.makeText(requireContext(), "Access Denied", Toast.LENGTH_LONG).show()
//            }
//        }
        findNavController().navigate(WorkerFragmentDirections
            .actionWorkerFragmentToWDetailsFragment(wList[position]))
    }

    fun loggingOut() {
        AuthUI.getInstance().signOut(requireContext())
        findNavController().navigate(R.id.action_workerFragment_to_loginFragment)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.logout_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> { confirmAction("Logging out, Confirm?") { loggingOut() }

            }
        }
        return super.onOptionsItemSelected(item)

    }

}