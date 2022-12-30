package com.example.vibeing.ui.authentication

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.fragment.app.Fragment
import com.example.vibeing.databinding.FragmentForgotPasswordBinding
import com.example.vibeing.utils.FormValidator
import com.example.vibeing.utils.FunctionUtils

class ForgotPasswordFragment : Fragment() {
    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater)
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
            submitBtn.setOnClickListener { validateForm() }
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
            //validate email
            val emailVerificationResult =
                FormValidator.validateEmail(requireContext(), emailEdit.text.toString())
            if (emailVerificationResult.isNotBlank()) {
                emailContainer.isErrorEnabled = true
                emailContainer.error = emailVerificationResult
                FunctionUtils.animateView(emailContainer)
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