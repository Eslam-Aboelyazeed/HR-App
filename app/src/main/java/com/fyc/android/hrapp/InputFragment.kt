package com.fyc.android.hrapp

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.fyc.android.hrapp.databinding.FragmentInputBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class InputFragment : Fragment() {

    private val workerCollectionRef = Firebase.firestore.collection("workers")
    private lateinit var _binding: FragmentInputBinding

    private lateinit var imgUri: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_input, container, false)

        imgUri = ""

        // Set the toolbar as the action bar
//        (activity as AppCompatActivity).setSupportActionBar(_binding.toolbar)

        // Set the toolbar title
//        _binding.toolbar.title = ""

//        // Set the toolbar navigation icon
//        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
//
//        // Set the toolbar listener
//        toolbar.setNavigationOnClickListener {
//            activity?.onBackPressed()
//        }

        _binding.addWButton.setOnClickListener {
            if (_binding.wFName.text.isNotEmpty() && _binding.wLName.text.isNotEmpty() &&
                _binding.dOB.text.isNotEmpty() && _binding.wSalary.text.isNotEmpty() &&
                _binding.phoneNum.text.isNotEmpty() && _binding.wCountry.text.isNotEmpty() &&
                _binding.wCity.text.isNotEmpty() && _binding.wGender.text.isNotEmpty() &&
                _binding.wNationality.text.isNotEmpty() && _binding.wNationalId.text.isNotEmpty() &&
                _binding.wHireDate.text.isNotEmpty() && _binding.wDepartment.text.isNotEmpty()) {
                if (_binding.wGender.text.toString() == "Male" || _binding.wGender.text.toString() == "Female") {
                    saveWorker(
                        Worker(
                            _binding.wFName.text.toString()
                                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                            _binding.wLName.text.toString()
                                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                            _binding.dOB.text.toString(),
                            _binding.wSalary.text.toString(),
                            _binding.phoneNum.text.toString(),
                            _binding.wCountry.text.toString()
                                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                            _binding.wCity.text.toString()
                                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                            _binding.wGender.text.toString()
                                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                            _binding.wNationality.text.toString()
                                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                            _binding.wNationalId.text.toString(),
                            _binding.wHireDate.text.toString(), 0, 0,
                            _binding.wDepartment.text.toString()
                                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                            "", "", "",
                            "", "", imgUri
                        )
                    )
                    findNavController().navigate(R.id.action_inputFragment_to_workerFragment)
                } else {
                    Toast.makeText(requireContext(),"Gender Must Be Male or Female", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(requireContext(),"Please Add The Required Info", Toast.LENGTH_LONG).show()
            }
        }

        _binding.wImgBtn.setOnClickListener {
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

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
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

}