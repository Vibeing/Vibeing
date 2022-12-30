package com.example.vibeing.ui.authentication

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.vibeing.R
import com.example.vibeing.databinding.FragmentUserDetailsBinding
import com.example.vibeing.models.User
import com.example.vibeing.ui.home.HomeActivity
import com.example.vibeing.utils.FormValidator
import com.example.vibeing.utils.FunctionUtils
import com.example.vibeing.utils.FunctionUtils.snackbar
import com.example.vibeing.utils.FunctionUtils.toast
import com.example.vibeing.utils.RequestStatus
import com.example.vibeing.viewModel.authentication.UserDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class UserDetailsFragment : Fragment() {
    private var _binding: FragmentUserDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var datePickerDialog: DatePickerDialog
    private val viewModel by viewModels<UserDetailViewModel>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUserDetailsBinding.inflate(inflater)
        focusScreen()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpGenderDropDown()
        setUpClickListener()
        handleLiveDataStatusChange()
    }

    private fun setUpClickListener() {
        with(binding) {
            dateOfBirthTxt.setOnClickListener { setUpDobCalender() }
            registerBtn.setOnClickListener { createUserProfile() }
        }
    }

    private fun handleLiveDataStatusChange() {
        viewModel.createUserLiveData.observe(viewLifecycleOwner) {
            with(binding) {
                when (it.status) {
                    RequestStatus.LOADING -> {
                        registerBtn.isClickable = false
                        registerBtnProgressBar.visibility = View.VISIBLE
                        registerBtnTxt.text = getString(R.string.verifying)
                    }
                    RequestStatus.SUCCESS -> {
                        registerBtn.isClickable = true
                        toast(requireContext(), getString(R.string.account_created_successfully))
                        startActivity(Intent(requireContext(), HomeActivity::class.java))
                        requireActivity().finish()
                    }
                    RequestStatus.EXCEPTION -> {
                        registerBtn.isClickable = true
                        registerBtnProgressBar.visibility = View.INVISIBLE
                        registerBtnTxt.text = getString(R.string.continue_txt)
                        snackbar(requireView(), it.message ?: getString(R.string.some_error_occurred)).show()
                    }
                }
            }
        }
    }

    private fun createUserProfile() {
        with(binding) {
            val fullName = fullNameEdit.text.toString().trim()
            val dob = dateOfBirthTxt.text.toString()
            val gender = genderTxt.text.toString()
            val user = User(fullName, gender, dob)
            if (!validateForm(user))
                return
            viewModel.createUserProfile(user)
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

    private fun validateForm(user: User): Boolean {
        with(binding) {
            fullNameContainer.isErrorEnabled = false
            dateOfBirthContainer.isErrorEnabled = false
            genderContainer.isErrorEnabled = false
            //validate full name
            val nameVerificationResult = FormValidator.validateName(requireContext(), user.fullName)
            if (nameVerificationResult.isNotBlank()) {
                fullNameContainer.isErrorEnabled = true
                fullNameContainer.error = nameVerificationResult
                FunctionUtils.animateView(fullNameContainer)
                FunctionUtils.vibrateDevice(requireContext())
                return false
            }
            //validate dob
            val dobVerificationResult = FormValidator.validateDateOfBirth(user.dob)
            if (!dobVerificationResult) {
                dateOfBirthContainer.isErrorEnabled = true
                dateOfBirthContainer.error = getString(R.string.please_select_dob)
                FunctionUtils.animateView(dateOfBirthContainer)
                FunctionUtils.vibrateDevice(requireContext())
                return false
            }
            //validate gender
            val genderVerificationResult = FormValidator.validateGender(requireContext(), user.gender)
            if (genderVerificationResult.isNotBlank()) {
                genderContainer.isErrorEnabled = true
                genderContainer.error = genderVerificationResult
                FunctionUtils.animateView(genderContainer)
                FunctionUtils.vibrateDevice(requireContext())
                return false
            }
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}