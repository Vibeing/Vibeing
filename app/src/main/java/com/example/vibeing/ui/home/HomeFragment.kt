package com.example.vibeing.ui.home

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.vibeing.R
import com.example.vibeing.databinding.FragmentHomeBinding
import com.example.vibeing.utils.FunctionUtils.setUpDialog
import com.example.vibeing.utils.FunctionUtils.toast
import com.example.vibeing.utils.RequestStatus
import com.example.vibeing.viewModel.home.GetCurrentViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val userViewModel by activityViewModels<GetCurrentViewModel>()
    private lateinit var userDialog: Dialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeBinding.inflate(inflater)
        handleCurrentUserData()
        initAll()
        getCurrentUser()
        return binding.root
    }

    private fun initAll() {
        userDialog = setUpDialog(getString(R.string.getting_user_details), requireContext())
    }

    private fun handleCurrentUserData() {
        userViewModel.currentUserLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                RequestStatus.LOADING -> {
                    userDialog.show()
                }
                RequestStatus.SUCCESS -> {
                    if (it.data != null)
                    else {
                        toast(requireContext(), getString(R.string.user_not_found))
                        Firebase.auth.signOut()
                        requireActivity().finish()
                    }
                    userDialog.hide()
                }
                RequestStatus.EXCEPTION -> {
                    userDialog.hide()
                    toast(requireContext(), getString(R.string.some_error_occurred))
                    requireActivity().finish()
                }
            }
        }
    }

    private fun getCurrentUser() {
        Firebase.auth.uid?.let { uid ->
            if (userViewModel.currentUserLiveData.value == null)
                userViewModel.getCurrentUser(uid)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}