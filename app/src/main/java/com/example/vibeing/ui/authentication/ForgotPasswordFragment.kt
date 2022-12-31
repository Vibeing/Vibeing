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
import com.example.vibeing.databinding.FragmentForgotPasswordBinding
import com.example.vibeing.utils.FormValidator
import com.example.vibeing.utils.FunctionUtils
import com.example.vibeing.utils.FunctionUtils.navigate
import com.example.vibeing.utils.FunctionUtils.snackbar
import com.example.vibeing.utils.RequestStatus
import com.example.vibeing.viewModel.authentication.ForgotPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {
    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ForgotPasswordViewModel>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater)
        focusScreen()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpClickListener()
        handleLiveDataStatusChange()
    }

    private fun setUpClickListener() {
        with(binding) {
            submitBtn.setOnClickListener { sendResetPasswordLink() }
        }
    }

    private fun sendResetPasswordLink() {
        with(binding) {
            val email = emailEdit.text.toString().trim()
            if (!validateForm(email))
                return
            viewModel.sendResetPasswordLink(email)
        }
    }

    private fun handleLiveDataStatusChange() {
        viewModel.sendResetPasswordLinkLiveData.observe(viewLifecycleOwner) {
            with(binding) {
                when (it.status) {
                    RequestStatus.LOADING -> {
                        submitBtn.isClickable = false
                        progressBar.visibility = View.VISIBLE
                        forgotPasswordBtnTxt.text = getString(R.string.verifying)
                    }
                    RequestStatus.SUCCESS -> {
                        submitBtn.isClickable = true
                        snackbar(requireView(), getString(R.string.a_reset_password_link_has_been_sent_to_the_registered_email_address)).show()
                        navigate(requireView(), R.id.action_forgotPasswordFragment_to_signinFragment)
                    }
                    RequestStatus.EXCEPTION -> {
                        submitBtn.isClickable = true
                        progressBar.visibility = View.INVISIBLE
                        forgotPasswordBtnTxt.text = getString(R.string.continue_txt)
                        snackbar(requireView(), it.message ?: getString(R.string.some_error_occurred)).show()
                    }
                }
            }
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

    private fun validateForm(email: String): Boolean {
        with(binding) {
            emailContainer.isErrorEnabled = false
            //validate email
            val emailVerificationResult =
                FormValidator.validateEmail(requireContext(), email)
            if (emailVerificationResult.isNotBlank()) {
                emailContainer.isErrorEnabled = true
                emailContainer.error = emailVerificationResult
                FunctionUtils.animateView(emailContainer)
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