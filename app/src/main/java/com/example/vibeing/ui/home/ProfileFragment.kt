package com.example.vibeing.ui.home

import android.app.Activity
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.vibeing.R
import com.example.vibeing.databinding.FragmentProfileBinding
import com.example.vibeing.utils.Constants.KEY_COVER_PIC
import com.example.vibeing.utils.Constants.KEY_PROFILE_PIC
import com.example.vibeing.utils.FunctionUtils.openGallery
import com.example.vibeing.utils.FunctionUtils.setUpDialog
import com.example.vibeing.utils.FunctionUtils.snackBar
import com.example.vibeing.utils.RequestStatus
import com.example.vibeing.viewModel.home.ProfileViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ProfileViewModel>()
    private lateinit var dialog: Dialog
    private var isProfilePictureClicked = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater)
        allowEnterTransitionOverlap = true
        allowReturnTransitionOverlap = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpClickListener()
        handleAddProfileImageToStorage()
        handleAddCoverImageToStorage()
    }

    private fun setUpClickListener() {
        with(binding) {
            changeProfileImg.setOnClickListener {
                isProfilePictureClicked = 1
                openGallery(resultLauncher)
            }

            changeCoverImg.setOnClickListener {
                isProfilePictureClicked = 0
                openGallery(resultLauncher)
            }
        }
    }

    private fun handleAddProfileImageToStorage() {
        viewModel.addProfileImageToServerLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                RequestStatus.LOADING -> {
                    dialog.show()
                }
                RequestStatus.SUCCESS -> {
                    dialog.hide()
                    viewModel.updateProfileOrCoverImage(Uri.parse(it.data), Firebase.auth.uid!!, KEY_PROFILE_PIC)
                }
                RequestStatus.EXCEPTION -> {
                    dialog.hide()
                    snackBar(requireView(), it.message ?: getString(R.string.some_error_occurred)).show()
                }
            }
        }
    }

    private fun handleAddCoverImageToStorage() {
        viewModel.addCoverImageToServerLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                RequestStatus.LOADING -> {
                    dialog.show()
                }
                RequestStatus.SUCCESS -> {
                    dialog.hide()
                    viewModel.updateProfileOrCoverImage(Uri.parse(it.data), Firebase.auth.uid!!, KEY_COVER_PIC)
                }
                RequestStatus.EXCEPTION -> {
                    dialog.hide()
                    snackBar(requireView(), it.message ?: getString(R.string.some_error_occurred)).show()
                }
            }
        }
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null && data.data != null) {
                // Log.e("abc", isProfilePictureClicked.toString() + " " + data.data)
                if (isProfilePictureClicked == 1) {
                    dialog = setUpDialog(getString(R.string.uploading_your_profile_photo), requireContext())
                    viewModel.addProfileImageToStorage(data.data!!, Firebase.auth.uid!!)
                } else if (isProfilePictureClicked == 0) {
                    dialog = setUpDialog(getString(R.string.uploading_your_cover_photo), requireContext())
                    viewModel.addCoverImageToStorage(data.data!!, Firebase.auth.uid!!)
                }
            }
        }
    }
}