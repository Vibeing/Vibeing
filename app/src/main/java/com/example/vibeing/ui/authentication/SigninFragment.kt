package com.example.vibeing.ui.authentication

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import com.example.vibeing.R
import com.example.vibeing.databinding.FragmentSigninBinding
import com.example.vibeing.ui.home.HomeActivity
import com.example.vibeing.utils.FormValidator
import com.example.vibeing.utils.FunctionUtils
import com.example.vibeing.utils.FunctionUtils.navigate
import com.example.vibeing.utils.RequestStatus
import com.example.vibeing.utils.Resource
import com.example.vibeing.viewModel.authentication.SigninViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SigninFragment : Fragment() {
    private var _binding: FragmentSigninBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<SigninViewModel>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSigninBinding.inflate(inflater)
        focusScreen()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpClickListener()
        handleSigninUserLiveDataStatusChange()
        handleCurrentUserLiveDataStatusChange()
    }

    private fun setUpClickListener() {
        with(binding) {
            signupTxt.setOnClickListener {
                navigate(requireView(), R.id.action_signinFragment_to_signupFragment)
            }
            forgotPasswordTxt.setOnClickListener {
                navigate(requireView(), R.id.action_signinFragment_to_forgotPasswordFragment)
            }
            signinBtn.setOnClickListener {
                it.hideKeyboard()
                signinUser()
            }
        }
    }

    private fun handleSigninUserLiveDataStatusChange() {
        viewModel.signinUserLiveData.observe(viewLifecycleOwner) {
            with(binding) {
                when (it.status) {
                    RequestStatus.LOADING -> {
                        signinBtn.isClickable = false
                        progressBar.visibility = View.VISIBLE
                        signinBtnTxt.text = getString(R.string.verifying)
                    }
                    RequestStatus.SUCCESS -> {
                        signinBtn.isClickable = true
                        Firebase.auth.uid?.let { it1 -> viewModel.checkCurrentUser(it1) }
                    }
                    RequestStatus.EXCEPTION -> {
                        signinBtn.isClickable = true
                        progressBar.visibility = View.INVISIBLE
                        signinBtnTxt.text = getString(R.string.continue_txt)
                        FunctionUtils.snackbar(requireView(), it.message ?: getString(R.string.some_error_occurred)).show()
                    }
                }
            }
        }
    }

    private fun handleCurrentUserLiveDataStatusChange() {
        viewModel.checkUserLiveData.observe(viewLifecycleOwner) {
            with(binding) {
                when (it.status) {
                    RequestStatus.LOADING -> {
                        signinBtn.isClickable = false
                        progressBar.visibility = View.VISIBLE
                        signinBtnTxt.text = getString(R.string.verifying)
                    }
                    RequestStatus.SUCCESS -> {
                        signinBtn.isClickable = true
                        signinBtnTxt.text = getText(R.string.continue_txt)
                        if (it.data == true) {
                            startActivity(Intent(requireContext(), HomeActivity::class.java))
                            requireActivity().finish()
                        } else {
                            val action = SigninFragmentDirections.actionSigninFragmentToUserDetailsFragment(binding.emailEdit.text.toString())
                            Navigation.findNavController(requireView()).navigate(action)
                        }
                    }
                    RequestStatus.EXCEPTION -> {
                        signinBtn.isClickable = true
                        Firebase.auth.signOut()
                        progressBar.visibility = View.INVISIBLE
                        signinBtnTxt.text = getString(R.string.continue_txt)
                        FunctionUtils.snackbar(requireView(), it.message ?: getString(R.string.some_error_occurred)).show()
                    }
                }
            }
        }
    }

    private fun signinUser() {
        with(binding) {
            val email = emailEdit.text.toString().trim()
            val password = passwordEdit.text.toString().trim()
            if (!validateForm(email, password))
                return
            viewModel.signinUser(email, password)
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

    private fun validateForm(email: String, password: String): Boolean {
        with(binding) {
            emailContainer.isErrorEnabled = false
            passwordContainer.isErrorEnabled = false
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
            //validate password
            val passwordValidationResult =
                FormValidator.validatePassword(requireContext(), password)
            if (passwordValidationResult.isNotBlank()) {
                passwordContainer.isErrorEnabled = true
                passwordContainer.error = passwordValidationResult
                FunctionUtils.animateView(passwordContainer)
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

    private fun View.hideKeyboard() {
        val inputManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onStop() {
        super.onStop()
        viewModel.signinUserLiveData = MutableLiveData<Resource<FirebaseUser>>()
        viewModel.checkUserLiveData = MutableLiveData<Resource<Boolean>>()
    }
}