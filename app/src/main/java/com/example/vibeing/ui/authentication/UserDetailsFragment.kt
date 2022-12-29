package com.example.vibeing.ui.authentication

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.vibeing.R
import com.example.vibeing.databinding.FragmentUserDetailsBinding
import com.example.vibeing.utils.FormValidator
import com.example.vibeing.utils.FunctionUtils
import java.util.*


class UserDetailsFragment : Fragment() {
    private var _binding: FragmentUserDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var datePickerDialog: DatePickerDialog
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUserDetailsBinding.inflate(inflater)
        focusScreen()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpGenderDropDown()
        setUpClickListener()
        return binding.root
    }

    private fun setUpClickListener() {
        with(binding) {
            dateOfBirthTxt.setOnClickListener { setUpDobCalender() }
            registerBtn.setOnClickListener { validateForm() }
        }
    }

    private fun setUpGenderDropDown() {
        val genderList = arrayOf(getString(R.string.male), getString(R.string.female), getString(R.string.other))
        val genderAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, genderList)
        binding.genderTxt.setAdapter(genderAdapter)
    }

    private fun setUpDobCalender() {
        val calender = Calendar.getInstance()
        val yrs = calender.get(Calendar.YEAR)
        val month = calender.get(Calendar.MONTH)
        val day = calender.get(Calendar.DAY_OF_MONTH)
        if ((!::datePickerDialog.isInitialized)) {
            datePickerDialog = DatePickerDialog(requireContext(), R.style.MyDatePickerStyle, datePickerResult, yrs, month, day)
            datePickerDialog.datePicker.maxDate = Date().time
        }
        datePickerDialog.show()
    }

    private val datePickerResult = DatePickerDialog.OnDateSetListener { datePicker, year, monthOfYear, dayOfMonth ->
        val date = "$dayOfMonth ${FunctionUtils.getMonthNameFromMonthNumber(monthOfYear)} $year"
        datePicker.updateDate(year, monthOfYear, dayOfMonth)
        binding.dateOfBirthTxt.setText(date)
    }

    private fun focusScreen() {
        binding.root.setOnApplyWindowInsetsListener { _, windowInsets ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val imeHeight = windowInsets.getInsets(WindowInsets.Type.ime()).bottom
                binding.root.setPadding(0, 0, 0, imeHeight)
            }
            windowInsets
        }
    }

    private fun validateForm() {
        with(binding) {
            fullNameContainer.isErrorEnabled = false
            dateOfBirthContainer.isErrorEnabled = false
            genderContainer.isErrorEnabled = false
            //validate full name
            val nameVerificationResult = FormValidator.validateName(requireContext(), fullNameEdit.text.toString())
            if (nameVerificationResult.isNotBlank()) {
                fullNameContainer.isErrorEnabled = true
                fullNameContainer.error = nameVerificationResult
                FunctionUtils.animateView(fullNameContainer)
                FunctionUtils.vibrateDevice(requireContext())
                return
            }
            //validate dob
            val dobVerificationResult = FormValidator.validateDateOfBirth(dateOfBirthTxt.text.toString())
            if (!dobVerificationResult) {
                dateOfBirthContainer.isErrorEnabled = true
                dateOfBirthContainer.error = getString(R.string.please_select_dob)
                FunctionUtils.animateView(dateOfBirthContainer)
                FunctionUtils.vibrateDevice(requireContext())
                return
            }
            //validate gender
            val genderVerificationResult = FormValidator.validateGender(requireContext(), genderTxt.text.toString())
            Log.e("abc", genderVerificationResult)
            if (genderVerificationResult.isNotBlank()) {
                genderContainer.isErrorEnabled = true
                genderContainer.error = genderVerificationResult
                FunctionUtils.animateView(genderContainer)
                FunctionUtils.vibrateDevice(requireContext())
                return
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}