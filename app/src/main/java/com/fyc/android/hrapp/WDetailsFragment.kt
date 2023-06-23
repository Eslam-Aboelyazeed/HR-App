package com.fyc.android.hrapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.fyc.android.hrapp.databinding.FragmentWDetailsBinding

class WDetailsFragment : Fragment() {

    private lateinit var _binding: FragmentWDetailsBinding

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_w_details, container, false)

        val worker = arguments?.let { WDetailsFragmentArgs.fromBundle(it).workersList }

        _binding.wName.text = worker?.fName + " " + worker?.lName
        _binding.wDob.text = worker?.dOB
        _binding.wPn.text = worker?.pNumber
        _binding.wSalary.text = worker?.salary

        return _binding.root
    }

}