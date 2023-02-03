package com.example.vibeing.ui.authentication

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.vibeing.R
import com.example.vibeing.databinding.FragmentUserDetailsBinding
import com.example.vibeing.models.User
import com.example.vibeing.ui.home.HomeActivity
import com.example.vibeing.utils.FormValidator.validateDateOfBirth
import com.example.vibeing.utils.FormValidator.validateGender
import com.example.vibeing.utils.FormValidator.validateName
import com.example.vibeing.utils.FunctionUtils.animateView
import com.example.vibeing.utils.FunctionUtils.focusScreen
import com.example.vibeing.utils.FunctionUtils.getMonthNameFromMonthNumber
import com.example.vibeing.utils.FunctionUtils.hideKeyboard
import com.example.vibeing.utils.FunctionUtils.snackBar
import com.example.vibeing.utils.FunctionUtils.toast
import com.example.vibeing.utils.FunctionUtils.vibrateDevice
import com.example.vibeing.utils.RequestStatus
import com.example.vibeing.viewModel.authentication.UserDetailViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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
        focusScreen(binding.root)
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
            registerBtn.setOnClickListener {
                createUserProfile()
            }
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
                        Firebase.auth.signOut()
                        registerBtnProgressBar.visibility = View.INVISIBLE
                        registerBtnTxt.text = getString(R.string.continue_txt)
                        snackBar(requireView(), it.message ?: getString(R.string.some_error_occurred)).show()
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
            val email = UserDetailsFragmentArgs.fromBundle(requireArguments()).email
            val user = User(fullName, email, gender, dob, "", "")
            if (!validateForm(user))
                return
            hideKeyboard(requireContext(), requireView())
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
        val date = "$dayOfMonth ${getMonthNameFromMonthNumber(monthOfYear)} $year"
        datePicker.updateDate(year, monthOfYear, dayOfMonth)
        binding.dateOfBirthTxt.setText(date)
    }

    private fun validateForm(user: User): Boolean {
        with(binding) {
            fullNameContainer.isErrorEnabled = false
            dateOfBirthContainer.isErrorEnabled = false
            genderContainer.isErrorEnabled = false
            //validate full name
            val nameVerificationResult = validateName(requireContext(), user.fullName)
            if (nameVerificationResult.isNotBlank()) {
                fullNameContainer.isErrorEnabled = true
                fullNameContainer.error = nameVerificationResult
                animateView(fullNameContainer)
                vibrateDevice(requireContext())
                return false
            }
            //validate dob
            val dobVerificationResult = validateDateOfBirth(user.dob)
            if (!dobVerificationResult) {
                dateOfBirthContainer.isErrorEnabled = true
                dateOfBirthContainer.error = getString(R.string.please_select_dob)
                animateView(dateOfBirthContainer)
                vibrateDevice(requireContext())
                return false
            }
            //validate gender
            val genderVerificationResult = validateGender(requireContext(), user.gender)
            if (genderVerificationResult.isNotBlank()) {
                genderContainer.isErrorEnabled = true
                genderContainer.error = genderVerificationResult
                animateView(genderContainer)
                vibrateDevice(requireContext())
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