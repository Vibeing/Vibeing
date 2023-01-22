package com.example.vibeing.ui.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.vibeing.R
import com.example.vibeing.databinding.FragmentForgotPasswordBinding
import com.example.vibeing.utils.FormValidator.validateEmail
import com.example.vibeing.utils.FunctionUtils.animateView
import com.example.vibeing.utils.FunctionUtils.focusScreen
import com.example.vibeing.utils.FunctionUtils.hideKeyboard
import com.example.vibeing.utils.FunctionUtils.navigate
import com.example.vibeing.utils.FunctionUtils.snackBar
import com.example.vibeing.utils.FunctionUtils.vibrateDevice
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
        focusScreen(binding.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpClickListener()
        handleLiveDataStatusChange()
    }

    private fun setUpClickListener() {
        with(binding) {
            submitBtn.setOnClickListener {
                sendResetPasswordLink()
            }
        }
    }

    private fun sendResetPasswordLink() {
        with(binding) {
            val email = emailEdit.text.toString().trim()
            if (!validateForm(email))
                return
            hideKeyboard(requireContext(), requireView())
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
                        snackBar(requireView(), getString(R.string.a_reset_password_link_has_been_sent_to_the_registered_email_address)).show()
                        navigate(requireView(), id = R.id.action_forgotPasswordFragment_to_signinFragment)
                    }
                    RequestStatus.EXCEPTION -> {
                        submitBtn.isClickable = true
                        progressBar.visibility = View.INVISIBLE
                        forgotPasswordBtnTxt.text = getString(R.string.continue_txt)
                        snackBar(requireView(), it.message ?: getString(R.string.some_error_occurred)).show()
                    }
                }
            }
        }
    }

    private fun validateForm(email: String): Boolean {
        with(binding) {
            emailContainer.isErrorEnabled = false
            //validate email
            val emailVerificationResult =
                validateEmail(requireContext(), email)
            if (emailVerificationResult.isNotBlank()) {
                emailContainer.isErrorEnabled = true
                emailContainer.error = emailVerificationResult
                animateView(emailContainer)
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