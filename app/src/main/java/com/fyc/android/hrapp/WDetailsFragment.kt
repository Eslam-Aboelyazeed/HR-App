package com.fyc.android.hrapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.fyc.android.hrapp.databinding.FragmentWDetailsBinding
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

class WDetailsFragment : Fragment() {

    private val workerCollectionRef = Firebase.firestore.collection("workers")

    private lateinit var _binding: FragmentWDetailsBinding

    private lateinit var worker: Worker

    private lateinit var user: String

    private lateinit var aList: ArrayList<Admin>

    private lateinit var admin: List<Admin>

    private lateinit var imgUri: String

    private val adminCollectionRef = Firebase.firestore.collection("admins")

//    private lateinit var animator0: TheAnimation
//
//    private lateinit var loadingRec: LoadingRec

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_w_details, container, false)

        imgUri = ""

        _binding.wFName.isFocusable= false
        _binding.wLName.isFocusable = false
        _binding.wDob.isFocusable = false
        _binding.wPn.isFocusable = false
        _binding.wSalary.isFocusable = false
        _binding.wCountry.isFocusable = false
        _binding.wCity.isFocusable = false
        _binding.wGender.isFocusable = false
        _binding.wNationality.isFocusable = false
        _binding.wNationalId.isFocusable = false
        _binding.wHireDate.isFocusable = false
        _binding.wDepartment.isFocusable = false
        _binding.changeImgBtn.isEnabled = false
//        _binding.changeImgBtn.isFocusable = false
//        _binding.changeImgBtn.isClickable = false
        _binding.changeImgBtn.setBackgroundColor(resources.getColor(R.color.gray))

        worker = arguments?.let { WDetailsFragmentArgs.fromBundle(it).workersList }!!

        _binding.wFName.setText(worker.fName)
        _binding.wLName.setText(worker.lName)
        _binding.wDob.setText(worker.dOB)
        _binding.wPn.setText(worker.pNumber)
        _binding.wSalary.setText(worker.salary)
        _binding.wCountry.setText(worker.country)
        _binding.wCity.setText(worker.city)
        _binding.wGender.setText(worker.gender)
        _binding.wNationality.setText(worker.nationality)
        _binding.wNationalId.setText(worker.nationalId)
        _binding.wHireDate.setText(worker.hireDate)
        _binding.wDepartment.setText(worker.department)

        Glide.with(requireContext())
            .load(Uri.parse(worker.imguri))
            .override(200,200)
            .error(R.drawable.ic_person_outline)
            .placeholder(R.drawable.ic_person_outline)
            .into(_binding.wImg)

        setHasOptionsMenu(true)

        _binding.applyEditFab.setOnClickListener {
            val editedWorker = getEditedWorker()
            updateWorker(worker, editedWorker)
            _binding.wFName.isFocusable= false
            _binding.wLName.isFocusable = false
            _binding.wDob.isFocusable = false
            _binding.wPn.isFocusable = false
            _binding.wSalary.isFocusable= false
            _binding.wCountry.isFocusable = false
            _binding.wCity.isFocusable = false
            _binding.wGender.isFocusable = false
            _binding.wNationality.isFocusable = false
            _binding.wNationalId.isFocusable = false
            _binding.wHireDate.isFocusable = false
            _binding.wDepartment.isFocusable = false
            _binding.changeImgBtn.isEnabled = false
//            _binding.changeImgBtn.isFocusable = false
//            _binding.changeImgBtn.isClickable = false
            _binding.applyEditFab.isClickable = false
            _binding.applyEditFab.visibility = View.INVISIBLE
            _binding.changeImgBtn.setBackgroundColor(resources.getColor(R.color.gray))
            Toast.makeText(requireContext(), "Successfully Edited", Toast.LENGTH_LONG).show()
        }

        aList = arrayListOf()

        admin = listOf()

        user = Firebase.auth.currentUser?.email ?: ""

        getAdminsLiveUpdates()

        _binding.wAttendedDaysBtn.setOnClickListener {
            findNavController().navigate(
                WDetailsFragmentDirections.actionWDetailsFragmentToWAttendedDaysFragment(worker))
        }

        _binding.changeImgBtn.setOnClickListener {
            // Check if we have permission to access the user's mobile storage
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Request permission
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            } else {
                // Pick an image from the user's mobile storage
                pickImage()
            }
        }

        return _binding.root
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

    fun getEditedWorker(): Map<String, Any> {
        val fName = _binding.wFName.text.toString()
        val lName = _binding.wLName.text.toString()
        val dOB = _binding.wDob.text.toString()
        val salary = _binding.wSalary.text.toString()
        val pNumber = _binding.wPn.text.toString()
        val country = _binding.wCountry.text.toString()
        val city = _binding.wCity.text.toString()
        val gender = _binding.wGender.text.toString()
        val nationality = _binding.wNationality.text.toString()
        val nationalId = _binding.wNationalId.text.toString()
        val hireDate = _binding.wHireDate.text.toString()
        val department = _binding.wDepartment.text.toString()
        val map = mutableMapOf<String, Any>()
        if (fName.isNotEmpty()) {
            map["fname"] = fName
        }
        if (lName.isNotEmpty()) {
            map["lname"] = lName
        }
        if (dOB.isNotEmpty()) {
            map["dob"] = dOB
        }
        if (salary.isNotEmpty()) {
            map["salary"] = salary
        }
        if (pNumber.isNotEmpty()) {
            map["pnumber"] = pNumber
        }
        if (country.isNotEmpty()) {
            map["country"] = country
        }
        if (city.isNotEmpty()) {
            map["city"] = city
        }
        if (gender.isNotEmpty()) {
            map["gender"] = gender
        }
        if (nationality.isNotEmpty()) {
            map["nationality"] = nationality
        }
        if (nationalId.isNotEmpty()) {
            map["nationalId"] = nationalId
        }
        if (hireDate.isNotEmpty()) {
            map["hireDate"] = hireDate
        }
        if (department.isNotEmpty()) {
            map["department"] = department
        }
        if (imgUri.isNotEmpty()) {
            map["imguri"] = imgUri
        }
        return map
    }

    fun updateWorker(worker: Worker, newWorkerMap: Map<String, Any>) = CoroutineScope(Dispatchers.IO).launch {
        val workerQuery = workerCollectionRef
            .whereEqualTo("fname", worker.fName)
            .whereEqualTo("lname", worker.lName)
            .whereEqualTo("dob", worker.dOB)
            .whereEqualTo("salary", worker.salary)
            .whereEqualTo("pnumber", worker.pNumber)
//            .whereEqualTo("country", worker.country)
//            .whereEqualTo("city", worker.city)
//            .whereEqualTo("gender", worker.gender)
//            .whereEqualTo("nationality", worker.nationality)
//            .whereEqualTo("nationalid", worker.nationalId)
//            .whereEqualTo("hiredate", worker.hireDate)
//            .whereEqualTo("department", worker.department)
            .get()
            .await()
        if (workerQuery.documents.isNotEmpty()){
            for (document in workerQuery){
                try {
                    workerCollectionRef.document(document.id).set(
                        newWorkerMap, SetOptions.merge()
                    ).await()

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

    fun deleteWorker(worker: Worker) = CoroutineScope(Dispatchers.IO).launch {
        val workerQuery = workerCollectionRef
            .whereEqualTo("fname", worker.fName)
            .whereEqualTo("lname", worker.lName)
            .whereEqualTo("dob", worker.dOB)
            .whereEqualTo("salary", worker.salary)
            .whereEqualTo("pnumber", worker.pNumber)
//            .whereEqualTo("country", worker.country)
//            .whereEqualTo("city", worker.city)
//            .whereEqualTo("gender", worker.gender)
//            .whereEqualTo("nationality", worker.nationality)
//            .whereEqualTo("nationalid", worker.nationalId)
//            .whereEqualTo("hiredate", worker.hireDate)
//            .whereEqualTo("department", worker.department)
            .get()
            .await()
        if (workerQuery.documents.isNotEmpty()){
            for (document in workerQuery){
                try {
                    workerCollectionRef.document(document.id).delete().await()
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Pick an image from the user's mobile storage
            pickImage()
        }
    }

    private fun pickImage() {
        // Create an intent to pick an image from the user's mobile storage
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        // Start the activity to pick an image
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            // Get the image URI
            imgUri = data.data!!.toString()

            Glide.with(requireContext())
                .load(Uri.parse(imgUri))
                .override(200,200)
                .error(R.drawable.ic_person_outline)
                .placeholder(R.drawable.ic_person_outline)
                .into(_binding.wImg)



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

    fun deletingWorker() {
        deleteWorker(worker);
        findNavController().navigate(R.id.action_WDetailsFragment_to_workerFragment)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater){

        inflater.inflate(R.menu.overflow_menu, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.delete_worker -> {for (a in admin) {
                if (a.type == "main" || a.type == "delete") {
                    confirmAction("Deleting Employee, Confirm?") {deletingWorker()}
                } else {
                    Toast.makeText(requireContext(), "Access Denied", Toast.LENGTH_LONG).show()
                }
            }
            }
            R.id.edit_worker -> {for (a in admin) {
                if (a.type == "main" || a.type == "edit") {
                    _binding.wFName.isFocusableInTouchMode = true;
                    _binding.wLName.isFocusableInTouchMode = true;
                    _binding.wDob.isFocusableInTouchMode = true;
                    _binding.wPn.isFocusableInTouchMode = true;
                    _binding.wSalary.isFocusableInTouchMode= true;
                    _binding.wCountry.isFocusableInTouchMode= true;
                    _binding.wCity.isFocusableInTouchMode= true;
                    _binding.wGender.isFocusableInTouchMode= true;
                    _binding.wNationality.isFocusableInTouchMode= true;
                    _binding.wNationalId.isFocusableInTouchMode= true;
                    _binding.wHireDate.isFocusableInTouchMode= true;
                    _binding.wDepartment.isFocusableInTouchMode= true;
                    _binding.changeImgBtn.isEnabled= true;
//                    _binding.changeImgBtn.isFocusableInTouchMode= true;
//                    _binding.changeImgBtn.isClickable = true;
                    _binding.applyEditFab.isClickable = true;
                    _binding.applyEditFab.visibility = View.VISIBLE;
                    _binding.changeImgBtn.setBackgroundColor(resources.getColor(R.color.teal_200))
                } else {
                    Toast.makeText(requireContext(), "Access Denied", Toast.LENGTH_LONG).show()
                }
            }
            }
            else -> return super.onContextItemSelected(item)
        }

        return true
    }

}