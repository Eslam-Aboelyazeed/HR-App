package com.fyc.android.hrapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.*
import android.widget.*
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
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class WDetailsFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private val workerCollectionRef = Firebase.firestore.collection("workers")

    private lateinit var _binding: FragmentWDetailsBinding

    private lateinit var worker: Worker

    private lateinit var user: String

    private lateinit var aList: ArrayList<Admin>

    private lateinit var admin: List<Admin>

    private lateinit var imgUri: String

    private val adminCollectionRef = Firebase.firestore.collection("admins")

    private lateinit var dobPickerDialog: DatePickerDialog
    private lateinit var hdPickerDialog: DatePickerDialog

//    private lateinit var animator0: TheAnimation
//
//    private lateinit var loadingRec: LoadingRec

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n", "SimpleDateFormat")
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
        _binding.wGender.isEnabled = false
        _binding.wNationality.isFocusable = false
        _binding.wNationalId.isFocusable = false
        _binding.wHireDate.isFocusable = false
        _binding.wDepartment.isEnabled = false
        _binding.changeImgBtn.isEnabled = false
        _binding.wDobPicker.isEnabled = false
        _binding.wHireDatePicker.isEnabled = false
        _binding.changeImgBtn.setBackgroundColor(resources.getColor(R.color.gray))
        _binding.wDobPicker.setBackgroundColor(resources.getColor(R.color.gray))
        _binding.wHireDatePicker.setBackgroundColor(resources.getColor(R.color.gray))
        _binding.wGender.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.gray)
        _binding.wDepartment.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.gray)
//        _binding.wGender.setBackgroundColor(resources.getColor(R.color.gray))
//        _binding.wDepartment.setBackgroundColor(resources.getColor(R.color.gray))
//        _binding.changeImgBtn.isFocusable = false
//        _binding.changeImgBtn.isClickable = false


        val options = arrayOf("Pick a Gender", "Male", "Female")

        val spinnerAdapter= object : ArrayAdapter<String>(requireContext(),android.R.layout.simple_spinner_item, options) {

            override fun isEnabled(position: Int): Boolean {
                // Disable the first item from Spinner
                // First item will be used for hint
                return position != 0
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view: TextView = super.getDropDownView(position, convertView, parent) as TextView
                view.setBackgroundColor(resources.getColor(R.color.purple_200))
                //set the color of first item in the drop down list to gray
                if(position == 0) {
                    view.setBackgroundColor(resources.getColor(R.color.DarkGrey))
                    view.setTextColor(Color.GRAY)
                } else {
                    //here it is possible to define color for other items by
                    view.setTextColor(Color.WHITE)
                }
                return view
            }

        }

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        _binding.wGender.adapter = spinnerAdapter

        _binding.wGender.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val value = parent!!.getItemAtPosition(position).toString()
                if(view != null) {
                    if(value == options[0]) {
                        (view as TextView).setTextColor(Color.WHITE)
                    } else {
                        (view as TextView).setTextColor(Color.WHITE)
                    }
//                    (view as TextView).setTextColor(Color.WHITE)
                }
            }
        }

        val items = arrayOf("Choose a Department", "Deb. A", "Deb. B", "Deb. C")

        val hSpinnerAdapter= object : ArrayAdapter<String>(requireContext(),android.R.layout.simple_spinner_item, items) {

            override fun isEnabled(position: Int): Boolean {
                // Disable the first item from Spinner
                // First item will be used for hint
                return position != 0
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view: TextView = super.getDropDownView(position, convertView, parent) as TextView
                view.setBackgroundColor(resources.getColor(R.color.purple_200))
                //set the color of first item in the drop down list to gray
                if(position == 0) {
                    view.setBackgroundColor(resources.getColor(R.color.DarkGrey))
                    view.setTextColor(Color.GRAY)
                } else {
                    //here it is possible to define color for other items by
                    view.setTextColor(Color.WHITE)
                }
                return view
            }

        }

        hSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        _binding.wDepartment.adapter = hSpinnerAdapter

        _binding.wDepartment.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val value = parent!!.getItemAtPosition(position).toString()
                if (view != null) {
                    if(value == items[0]) {
                        (view as TextView).setTextColor(Color.WHITE)
                    } else {
                        (view as TextView).setTextColor(Color.WHITE)
                    }
//                    (view as TextView).setTextColor(Color.WHITE)
                }
            }
        }

        worker = arguments?.let { WDetailsFragmentArgs.fromBundle(it).workersList }!!

        val gIndex = options.indexOf(worker.gender)

        val hIndex = items.indexOf(worker.department)

        _binding.wFName.setText(worker.fName)
        _binding.wLName.setText(worker.lName)
        _binding.wDob.setText(worker.dOB)
        _binding.wPn.setText(worker.pNumber)
        _binding.wSalary.setText(worker.salary)
        _binding.wCountry.setText(worker.country)
        _binding.wCity.setText(worker.city)
        _binding.wGender.setSelection(gIndex)
        _binding.wNationality.setText(worker.nationality)
        _binding.wNationalId.setText(worker.nationalId)
        _binding.wHireDate.setText(worker.hireDate)
        _binding.wDepartment.setSelection(hIndex)

        Glide.with(requireContext())
            .load(Uri.parse(worker.imguri))
            .override(200,200)
            .error(R.drawable.ic_person_outline)
            .placeholder(R.drawable.ic_person_outline)
            .into(_binding.wImg)

        setHasOptionsMenu(true)

        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

        val parsedDobDate = simpleDateFormat.parse(_binding.wDob.text.toString())

        val dCalendar = Calendar.getInstance()

        if (parsedDobDate != null) {
            dCalendar.time = parsedDobDate
        }

//        dCalendar.set(parsedDobDate?.year ?: Calendar.YEAR,
//                    parsedDobDate?.month ?: Calendar.MONTH,
//                     parsedDobDate?.day ?: Calendar.DAY_OF_MONTH
//        )

        _binding.wDobPicker.setOnClickListener {
            dobPickerDialog = DatePickerDialog(
                requireContext(),
                R.style.Theme_AppCompat_DayNight_Dialog_Alert,
                this,
                dCalendar.get(Calendar.YEAR),
                dCalendar.get(Calendar.MONTH),
                dCalendar.get(Calendar.DAY_OF_MONTH)
            )

            dobPickerDialog.show()
        }

        val parsedHdDate = simpleDateFormat.parse(_binding.wHireDate.text.toString())

        val hdCalendar = Calendar.getInstance()

        if (parsedHdDate != null) {
            hdCalendar.time = parsedHdDate
        }

        _binding.wHireDatePicker.setOnClickListener {
            hdPickerDialog = DatePickerDialog(
                requireContext(),
                R.style.Theme_AppCompat_DayNight_Dialog_Alert,
                this,
                hdCalendar.get(Calendar.YEAR),
                hdCalendar.get(Calendar.MONTH),
                hdCalendar.get(Calendar.DAY_OF_MONTH)
            )

            hdPickerDialog.show()
        }

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
            _binding.wGender.isEnabled = false
            _binding.wNationality.isFocusable = false
            _binding.wNationalId.isFocusable = false
            _binding.wHireDate.isFocusable = false
            _binding.wDepartment.isEnabled = false
            _binding.changeImgBtn.isEnabled = false
            _binding.wDobPicker.isEnabled = false
            _binding.wHireDatePicker.isEnabled = false
//            _binding.changeImgBtn.isFocusable = false
//            _binding.changeImgBtn.isClickable = false
            _binding.applyEditFab.isClickable = false
            _binding.applyEditFab.visibility = View.INVISIBLE
            _binding.changeImgBtn.setBackgroundColor(resources.getColor(R.color.gray))
            _binding.wDobPicker.setBackgroundColor(resources.getColor(R.color.gray))
            _binding.wHireDatePicker.setBackgroundColor(resources.getColor(R.color.gray))
            _binding.wGender.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.gray)
            _binding.wDepartment.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.gray)
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
        val gender = _binding.wGender.selectedItem.toString()
        val nationality = _binding.wNationality.text.toString()
        val nationalId = _binding.wNationalId.text.toString()
        val hireDate = _binding.wHireDate.text.toString()
        val department = _binding.wDepartment.selectedItem.toString()
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
//                    _binding.wDob.isFocusableInTouchMode = true;
                    _binding.wPn.isFocusableInTouchMode = true;
                    _binding.wSalary.isFocusableInTouchMode= true;
                    _binding.wCountry.isFocusableInTouchMode= true;
                    _binding.wCity.isFocusableInTouchMode= true;
                    _binding.wGender.isEnabled= true;
                    _binding.wNationality.isFocusableInTouchMode= true;
                    _binding.wNationalId.isFocusableInTouchMode= true;
//                    _binding.wHireDate.isFocusableInTouchMode= true;
                    _binding.wDepartment.isEnabled= true;
                    _binding.changeImgBtn.isEnabled= true;
                    _binding.wDobPicker.isEnabled= true;
                    _binding.wHireDatePicker.isEnabled= true;
//                    _binding.changeImgBtn.isFocusableInTouchMode= true;
//                    _binding.changeImgBtn.isClickable = true;
                    _binding.applyEditFab.isClickable = true;
                    _binding.applyEditFab.visibility = View.VISIBLE;
                    _binding.changeImgBtn.setBackgroundColor(resources.getColor(R.color.teal_200))
                    _binding.wDobPicker.setBackgroundColor(resources.getColor(R.color.teal_200))
                    _binding.wHireDatePicker.setBackgroundColor(resources.getColor(R.color.teal_200))
                    _binding.wGender.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.purple_200)
                    _binding.wDepartment.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.purple_200)
                } else {
                    Toast.makeText(requireContext(), "Access Denied", Toast.LENGTH_LONG).show()
                }
            }
            }
            else -> return super.onContextItemSelected(item)
        }

        return true
    }

    @SuppressLint("SimpleDateFormat")
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        if (view == dobPickerDialog.datePicker) {
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)

            val formattedDateString = SimpleDateFormat("dd/MM/yyyy").format(selectedDate.time)

            _binding.wDob.setText(formattedDateString)

        } else if (view == hdPickerDialog.datePicker) {
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)

            val formattedDateString = SimpleDateFormat("dd/MM/yyyy").format(selectedDate.time)

            _binding.wHireDate.setText(formattedDateString)
        }
    }

}