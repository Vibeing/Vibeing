package com.example.vibeing.ui.authentication

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.fragment.app.Fragment
import com.example.vibeing.R
import com.example.vibeing.databinding.FragmentSigninBinding
import com.example.vibeing.utils.FunctionUtils.navigate

class SigninFragment : Fragment() {
    private lateinit var binding: FragmentSigninBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSigninBinding.inflate(inflater)
        focusScreen()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAll()
        setUpClickListener()
    }

    private fun setUpClickListener() {
        with(binding){
            signupTxt.setOnClickListener {
                navigate(requireView(),R.id.action_signinFragment_to_signupFragment)
            }
            forgotPasswordTxt.setOnClickListener {
                navigate(requireView(),R.id.action_signinFragment_to_forgotPasswordFragment)
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

    private fun initAll() {

    }

}