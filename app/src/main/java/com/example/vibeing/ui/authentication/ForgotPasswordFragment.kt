package com.example.vibeing.ui.authentication

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import com.example.vibeing.R
import com.example.vibeing.databinding.FragmentForgotPasswordBinding


class ForgotPasswordFragment : Fragment() {
 private lateinit var binding: FragmentForgotPasswordBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding= FragmentForgotPasswordBinding.inflate(inflater)
        focusScreen()
        initAll()
        return binding.root
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