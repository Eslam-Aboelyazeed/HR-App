package com.fyc.android.hrapp

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
//import androidx.core.content.ContextCompat.getColorStateList
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import com.fyc.android.hrapp.databinding.FragmentWorkerBinding
import com.google.android.material.chip.Chip
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

    private lateinit var wl: List<Worker>

    private lateinit var fl: List<Worker>

    private lateinit var colorStateList: ColorStateList

    private val adminCollectionRef = Firebase.firestore.collection("admins")

    private val workerCollectionRef = Firebase.firestore.collection("workers")

    @SuppressLint("SetTextI18n", "ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_worker, container, false)


//        _binding.chipGroup.clearCheck()
//        _binding.chipAll.isChecked = true
//        _binding.chipA.isChecked = false
//        _binding.chipB.isChecked = false
//        _binding.chipC.isChecked = false

        //fragmentManager?.let { clearBackStack(it) }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }

        RV = _binding.workersList

//        val manager = LinearLayoutManager(activity)

        val manager = GridLayoutManager(activity, 2)

        RV.layoutManager = manager

        RV.setHasFixedSize(true)

        wList = Workers()

        aList = arrayListOf()

        wl = listOf()

        fl = listOf()

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

        getLiveUpdates()
//        val listener: CompoundButton.OnCheckedChangeListener = CompoundButton.OnCheckedChangeListener() { compoundButton: CompoundButton, b: Boolean ->
//
//        }
//        _binding.chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
//            val selectedFilters = checkedIds.map { group.findViewById<Chip>(it).text.toString() }
//
//            when (selectedFilters) {
//                listOf("chipAll") -> {RV.adapter = WRV(this, arrayListOf(*wl.toTypedArray()), requireContext())
//                _binding.chipAll.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.teal_700)}
//                listOf("chipA") -> {filterByDepartment("Deb. A")
//                _binding.chipA.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.teal_700)}
//                listOf("chipB") -> {filterByDepartment("Deb. B")
//                _binding.chipB.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.teal_700)}
//                listOf("chipC") -> {filterByDepartment("Deb. C")
//                _binding.chipC.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.teal_700)}
//                else -> RV.adapter = WRV(this, arrayListOf(*wl.toTypedArray()), requireContext())
//            }
//        }

        colorStateList = ContextCompat.getColorStateList(requireContext(), R.drawable.color_state_list)!!
//        _binding.chipAll.chipBackgroundColor = colorStateList
//        _binding.chipA.chipBackgroundColor = colorStateList
//        _binding.chipB.chipBackgroundColor = colorStateList
//        _binding.chipC.chipBackgroundColor = colorStateList

//        if (_binding.chipAll.isChecked) {
//            fl = wl
//            RV.adapter = WRV(this, arrayListOf(*fl.toTypedArray()), requireContext())
//            _binding.chipA.chipBackgroundColor = colorStateList
//            _binding.chipB.chipBackgroundColor = colorStateList
//            _binding.chipC.chipBackgroundColor = colorStateList
//            _binding.allEmployees.text = "All Employees"
//        } else if (_binding.chipA.isChecked) {
//            filterByDepartment("Deb. A")
//            _binding.chipAll.chipBackgroundColor = colorStateList
//            _binding.chipB.chipBackgroundColor = colorStateList
//            _binding.chipC.chipBackgroundColor = colorStateList
//            _binding.allEmployees.text = "Deb. A Employees"
//        } else if (_binding.chipB.isChecked) {
//            filterByDepartment("Deb. B")
//            _binding.chipA.chipBackgroundColor = colorStateList
//            _binding.chipAll.chipBackgroundColor = colorStateList
//            _binding.chipC.chipBackgroundColor = colorStateList
//            _binding.allEmployees.text = "Deb. B Employees"
//        } else if (_binding.chipC.isChecked) {
//            filterByDepartment("Deb. C")
//            _binding.chipA.chipBackgroundColor = colorStateList
//            _binding.chipB.chipBackgroundColor = colorStateList
//            _binding.chipAll.chipBackgroundColor = colorStateList
//            _binding.allEmployees.text = "Deb. C Employees"
//        } else {
//            fl = wl
//            RV.adapter = WRV(this, arrayListOf(*fl.toTypedArray()), requireContext())
//            _binding.chipAll.chipBackgroundColor = colorStateList
//            _binding.chipA.chipBackgroundColor = colorStateList
//            _binding.chipB.chipBackgroundColor = colorStateList
//            _binding.chipC.chipBackgroundColor = colorStateList
//            _binding.allEmployees.text = "All Employees"
//        }

        _binding.chipAll.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                _binding.chipAll.isEnabled = false
                _binding.chipAll.isClickable = false
                //_binding.chipAll.isFocusable = false
                fl = wl
                RV.adapter = WRV(this, arrayListOf(*fl.toTypedArray()), requireContext())
                _binding.chipA.chipBackgroundColor = colorStateList
                _binding.chipB.chipBackgroundColor = colorStateList
                _binding.chipC.chipBackgroundColor = colorStateList
                _binding.allEmployees.text = "All Employees"
            } else {
                _binding.chipAll.isEnabled = true
                _binding.chipAll.isClickable = true
                //_binding.chipAll.isFocusableInTouchMode = true
            }
        }

        _binding.chipA.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                filterByDepartment("Deb. A")
                _binding.chipA.isEnabled = false
                _binding.chipA.isClickable = false
                //_binding.chipA.isFocusable = false
                filterByDepartment("Deb. A")
                _binding.chipAll.chipBackgroundColor = colorStateList
                _binding.chipB.chipBackgroundColor = colorStateList
                _binding.chipC.chipBackgroundColor = colorStateList
                _binding.allEmployees.text = "Deb. A Employees"
                filterByDepartment("Deb. A")
            } else {
                _binding.chipA.isEnabled = true
                _binding.chipA.isClickable = true
                //_binding.chipA.isFocusableInTouchMode = true
            }
        }

        _binding.chipB.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                _binding.chipB.isEnabled = false
                _binding.chipB.isClickable = false
               // _binding.chipB.isFocusable = false
                filterByDepartment("Deb. B")
                _binding.chipA.chipBackgroundColor = colorStateList
                _binding.chipAll.chipBackgroundColor = colorStateList
                _binding.chipC.chipBackgroundColor = colorStateList
                _binding.allEmployees.text = "Deb. B Employees"
            } else {
                _binding.chipB.isEnabled = true
                _binding.chipB.isClickable = true
               // _binding.chipB.isFocusableInTouchMode = true
            }
        }

        _binding.chipC.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                _binding.chipC.isEnabled = false
                _binding.chipC.isClickable = false
                //_binding.chipC.isFocusable = false
                filterByDepartment("Deb. C")
                _binding.chipA.chipBackgroundColor = colorStateList
                _binding.chipB.chipBackgroundColor = colorStateList
                _binding.chipAll.chipBackgroundColor = colorStateList
                _binding.allEmployees.text = "Deb. C Employees"
//                Toast.makeText(requireContext(), fl.toString(), Toast.LENGTH_LONG).show()
            } else {
                _binding.chipC.isEnabled = true
                _binding.chipC.isClickable = true
                //_binding.chipC.isFocusableInTouchMode = true
            }
        }

//        if (_binding.chipAll.isChecked) {
//            _binding.chipAll.isEnabled = false
//            _binding.chipAll.isClickable = false
//            _binding.chipAll.isFocusable = false
//        } else {
            _binding.chipAll.setOnClickListener {
                fl = wl
                RV.adapter = WRV(this, arrayListOf(*fl.toTypedArray()), requireContext())
                _binding.chipA.chipBackgroundColor = colorStateList
                _binding.chipB.chipBackgroundColor = colorStateList
                _binding.chipC.chipBackgroundColor = colorStateList
                _binding.allEmployees.text = "All Employees"
//            _binding.chipAll.chipBackgroundColor = colorStateList
//            _binding.chipAll.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.teal_700)
            }
//        }

//        if (_binding.chipA.isChecked) {
//            _binding.chipA.isEnabled = false
//            _binding.chipA.isClickable = false
//            _binding.chipA.isFocusable = false
//        } else {
            _binding.chipA.setOnClickListener {
                //Toast.makeText(requireContext(), "test", Toast.LENGTH_LONG).show()
                filterByDepartment("Deb. A")
                _binding.chipAll.chipBackgroundColor = colorStateList
                _binding.chipB.chipBackgroundColor = colorStateList
                _binding.chipC.chipBackgroundColor = colorStateList
                _binding.allEmployees.text = "Deb. A Employees"
//            _binding.chipA.chipBackgroundColor = colorStateList
//            _binding.chipA.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.teal_700)
            }
//        }

//        if (_binding.chipB.isChecked) {
//            _binding.chipB.isEnabled = false
//            _binding.chipB.isClickable = false
//            _binding.chipB.isFocusable = false
//        } else {
            _binding.chipB.setOnClickListener {
                filterByDepartment("Deb. B")
                _binding.chipA.chipBackgroundColor = colorStateList
                _binding.chipAll.chipBackgroundColor = colorStateList
                _binding.chipC.chipBackgroundColor = colorStateList
                _binding.allEmployees.text = "Deb. B Employees"
//            _binding.chipB.chipBackgroundColor = colorStateList
//            _binding.chipB.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.teal_700)
            }
//        }

//        if (_binding.chipC.isChecked) {
//            _binding.chipC.isEnabled = false
//            _binding.chipC.isClickable = false
//            _binding.chipC.isFocusable = false
//        } else {
            _binding.chipC.setOnClickListener {
                filterByDepartment("Deb. C")
                _binding.chipA.chipBackgroundColor = colorStateList
                _binding.chipB.chipBackgroundColor = colorStateList
                _binding.chipAll.chipBackgroundColor = colorStateList
                _binding.allEmployees.text = "Deb. C Employees"
//            _binding.chipC.chipBackgroundColor = colorStateList
//            _binding.chipC.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.teal_700)
//            _binding.chipC.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.purple_200)
//            _binding.chipC.setBackgroundColor(resources.getColor(R.color.purple_200))
            }
//        }

//        _binding.cFab.setOnClickListener {
//            findNavController().navigate(R.id.action_workerFragment_to_calendarFragment)
//        }
//
//        _binding.sfab.setOnClickListener {
//            findNavController().navigate(R.id.action_workerFragment_to_salaryFragment)
//        }

        setHasOptionsMenu(true)

        return _binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        _binding.chipGroup.clearCheck()

//        if (_binding.chipAll.isChecked) {
//            fl = wl
//            RV.adapter = WRV(this, arrayListOf(*fl.toTypedArray()), requireContext())
//            _binding.chipA.chipBackgroundColor = colorStateList
//            _binding.chipB.chipBackgroundColor = colorStateList
//            _binding.chipC.chipBackgroundColor = colorStateList
//            _binding.allEmployees.text = "All Employees"
//        } else if (_binding.chipA.isChecked) {
//            filterByDepartment("Deb. A")
//            _binding.chipAll.chipBackgroundColor = colorStateList
//            _binding.chipB.chipBackgroundColor = colorStateList
//            _binding.chipC.chipBackgroundColor = colorStateList
//            _binding.allEmployees.text = "Deb. A Employees"
//        } else if (_binding.chipB.isChecked) {
//            filterByDepartment("Deb. B")
//            _binding.chipA.chipBackgroundColor = colorStateList
//            _binding.chipAll.chipBackgroundColor = colorStateList
//            _binding.chipC.chipBackgroundColor = colorStateList
//            _binding.allEmployees.text = "Deb. B Employees"
//        } else if (_binding.chipC.isChecked) {
//            filterByDepartment("Deb. C")
//            _binding.chipA.chipBackgroundColor = colorStateList
//            _binding.chipB.chipBackgroundColor = colorStateList
//            _binding.chipAll.chipBackgroundColor = colorStateList
//            _binding.allEmployees.text = "Deb. C Employees"
//            Toast.makeText(requireContext(), fl.toString(), Toast.LENGTH_LONG).show()
//        } else {
//            fl = wl
//            RV.adapter = WRV(this, arrayListOf(*fl.toTypedArray()), requireContext())
//            _binding.chipAll.chipBackgroundColor = colorStateList
//            _binding.chipA.chipBackgroundColor = colorStateList
//            _binding.chipB.chipBackgroundColor = colorStateList
//            _binding.chipC.chipBackgroundColor = colorStateList
//            _binding.allEmployees.text = "All Employees"
//        }

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

    private fun filterByDepartment(dep:String) {
        fl = wl.filter { it.department == dep }
        //Toast.makeText(requireContext(), wl.toString(), Toast.LENGTH_LONG).show()
        RV.adapter = WRV(this, arrayListOf(*fl.toTypedArray()), requireContext())
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

                }
            }
            wl = wList.sortedBy { it.fName }
            fl = wl
            if (_binding.chipAll.isChecked) {
                RV.adapter = WRV(this, arrayListOf(*fl.toTypedArray()), requireContext())
            } else if (_binding.chipA.isChecked) {
                filterByDepartment("Deb. A")
            }else if (_binding.chipB.isChecked) {
                filterByDepartment("Deb. B")
            }else if (_binding.chipC.isChecked) {
                filterByDepartment("Deb. C")
            }else {
                RV.adapter = WRV(this, arrayListOf(*fl.toTypedArray()), requireContext())
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

    fun clearBackStack(fragmentManager: FragmentManager) {
        if (fragmentManager.backStackEntryCount > 1) {
            val entry = fragmentManager.getBackStackEntryAt(1)
            fragmentManager.popBackStack(entry.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
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
            .actionWorkerFragmentToWDetailsFragment(fl[position]))
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