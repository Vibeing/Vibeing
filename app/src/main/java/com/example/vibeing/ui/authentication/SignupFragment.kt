package com.example.vibeing.ui.authentication


import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.vibeing.R
import com.example.vibeing.databinding.FragmentSignupBinding
import com.example.vibeing.utils.FormValidator.validateConfirmPassword
import com.example.vibeing.utils.FormValidator.validateEmail
import com.example.vibeing.utils.FormValidator.validatePassword
import com.example.vibeing.utils.FunctionUtils.animateView
import com.example.vibeing.utils.FunctionUtils.navigate
import com.example.vibeing.utils.FunctionUtils.snackbar
import com.example.vibeing.utils.FunctionUtils.vibrateDevice
import com.example.vibeing.utils.RequestStatus
import com.example.vibeing.viewModel.authentication.SignupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<SignupViewModel>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSignupBinding.inflate(layoutInflater)
        focusScreen()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpClickListener()
        handleLiveDataStatusChange()
    }

    private fun handleLiveDataStatusChange() {
        viewModel.signupUserLiveData.observe(viewLifecycleOwner) {
            with(binding) {
                when (it.status) {
                    RequestStatus.LOADING -> {
                        continueBtn.isClickable = false
                        progressBar.visibility = View.VISIBLE
                        continueBtnTxt.text = getString(R.string.verifying)
                    }
                    RequestStatus.SUCCESS -> {
                        continueBtn.isClickable = true
                        navigate(requireView(), R.id.action_signupFragment_to_userDetailsFragment)
                    }
                    RequestStatus.EXCEPTION -> {
                        continueBtn.isClickable = true
                        progressBar.visibility = View.INVISIBLE
                        continueBtnTxt.text = getString(R.string.continue_txt)
                        snackbar(requireView(), it.message ?: getString(R.string.some_error_occurred)).show()
                    }
                }
            }
        }
    }

    private fun setUpClickListener() {
        with(binding) {
            continueBtn.setOnClickListener { signupUser() }
            signinTxt.setOnClickListener { navigate(requireView(), R.id.action_signupFragment_to_signinFragment) }
        }
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

    private fun signupUser() {
        with(binding) {
            val email = emailEdit.text.toString().trim()
            val password = passwordEdit.text.toString().trim()
            val confirmPassword = confirmPasswordEdit.text.toString().trim()
            if (!validateForm(email, password, confirmPassword))
                return
            viewModel.signupUser(email, password)
        }
    }

    private fun validateForm(email: String, password: String, confirmPassword: String): Boolean {
        with(binding) {
            emailContainer.isErrorEnabled = false
            passwordContainer.isErrorEnabled = false
            confirmPasswordContainer.isErrorEnabled = false
            //validate email
            val emailVerificationResult = validateEmail(requireContext(), email)
            if (emailVerificationResult.isNotBlank()) {
                emailContainer.isErrorEnabled = true
                emailContainer.error = emailVerificationResult
                animateView(emailContainer)
                vibrateDevice(requireContext())
                return false
            }
            //validate password
            val passwordValidationResult = validatePassword(requireContext(), password)
            if (passwordValidationResult.isNotBlank()) {
                passwordContainer.isErrorEnabled = true
                passwordContainer.error = passwordValidationResult
                animateView(passwordContainer)
                vibrateDevice(requireContext())
                return false
            }
            //validate confirm password
            val confirmPasswordValidationResult = validateConfirmPassword(password, confirmPassword)
            if (!confirmPasswordValidationResult) {
                confirmPasswordContainer.isErrorEnabled = true
                confirmPasswordContainer.error = getString(R.string.password_not_match)
                animateView(confirmPasswordContainer)
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