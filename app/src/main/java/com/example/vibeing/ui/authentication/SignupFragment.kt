package com.example.vibeing.ui.authentication


import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.fragment.app.Fragment
import com.example.vibeing.databinding.FragmentSignupBinding
import com.example.vibeing.utils.FormValidator.validateConfirmPassword
import com.example.vibeing.utils.FormValidator.validateEmail
import com.example.vibeing.utils.FormValidator.validatePassword
import com.example.vibeing.utils.FunctionUtils.animateView
import com.example.vibeing.utils.FunctionUtils.vibrateDevice


class SignupFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSignupBinding.inflate(layoutInflater)
        focusScreen()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAll()
        setUpClickListener()
    }

    private fun setUpClickListener() {
        with(binding) {
            continueBtn.setOnClickListener { validateForm() }
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

    private fun initAll() {

    }

    private fun validateForm() {
        with(binding) {
            emailContainer.isErrorEnabled = false
            passwordContainer.isErrorEnabled = false
            confirmPasswordContainer.isErrorEnabled = false
            //validate email
            val emailVerificationResult = validateEmail(requireContext(), emailEdit.text.toString())
            if (emailVerificationResult.isNotBlank()) {
                emailContainer.isErrorEnabled = true
                emailContainer.error = emailVerificationResult
                animateView(emailContainer)
                vibrateDevice(requireContext())
                return
            }
            //validate password
            val passwordValidationResult = validatePassword(requireContext(), passwordEdit.text.toString())
            if (passwordValidationResult.isNotBlank()) {
                passwordContainer.isErrorEnabled = true
                passwordContainer.error = passwordValidationResult
                animateView(passwordContainer)
                vibrateDevice(requireContext())
                return
            }
            //validate confirm password
            val confirmPasswordValidationResult = validateConfirmPassword(passwordEdit.text.toString(), confirmPasswordEdit.text.toString())
            if (!confirmPasswordValidationResult) {
                confirmPasswordContainer.isErrorEnabled = true
                confirmPasswordContainer.error = "Password do not match"
                animateView(confirmPasswordContainer)
                vibrateDevice(requireContext())
                return
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}