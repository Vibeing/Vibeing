package com.example.vibeing.ui.home

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.vibeing.R
import com.example.vibeing.adapters.home.ProfilePostsAdapter
import com.example.vibeing.databinding.FragmentProfileBinding
import com.example.vibeing.models.User
import com.example.vibeing.utils.FunctionUtils.openGallery
import com.example.vibeing.utils.FunctionUtils.setUpDialog
import com.example.vibeing.utils.FunctionUtils.snackBar
import com.example.vibeing.utils.RequestStatus
import com.example.vibeing.utils.Resource
import com.example.vibeing.viewModel.home.GetCurrentViewModel
import com.example.vibeing.viewModel.home.ProfileViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ProfileViewModel>()
    private val userViewModel by activityViewModels<GetCurrentViewModel>()
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
        loadCurrentUserDetails()
        loadCurrentUserPosts()
        handleCurrentUserPosts()
        handleAddProfileImageToStorage()
        handleAddCoverImageToStorage()
        handleUpdateUserDetails()
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

    private fun loadCurrentUserDetails() {
        with(binding) {
            userViewModel.currentUserLiveData.value?.data?.let { user ->
                if (user.coverPic.isNotBlank())
                    Picasso.get().load(user.coverPic).placeholder(R.drawable.ic_default_user).into(coverImage)
                if (user.profilePic.isNotBlank())
                    Picasso.get().load(user.profilePic).placeholder(R.drawable.ic_default_user).into(profileImage)
                userNameTxt.text = user.fullName
                userBioTxt.text = user.bio
            }
        }
    }

    private fun loadCurrentUserPosts() {
        viewModel.getUserPosts(Firebase.auth.uid!!)
    }

    private fun handleCurrentUserPosts() {
        viewModel.getUserPostsLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                RequestStatus.LOADING -> {
                }
                RequestStatus.SUCCESS -> {
                    with(binding) {
                        val postsList = it.data
                        if (postsList?.isEmpty() == true) {
                            noPostTxt.visibility = View.VISIBLE
                            seeAllPostsBtn.visibility = View.GONE
                            postsRecyclerView.visibility = View.GONE
                            postsTxt.text = getText(R.string.posts)
                        } else {
                            postsRecyclerView.visibility = View.VISIBLE
                            noPostTxt.visibility = View.GONE
                            postsTxt.text = String.format(getString(R.string.post_count, postsList?.size.toString()))
                            if (postsList?.size?.let { size -> size > 8 } == true)
                                seeAllPostsBtn.visibility = View.VISIBLE
                            else
                                seeAllPostsBtn.visibility = View.GONE
                            val postsAdapter = postsList?.let { list -> ProfilePostsAdapter(requireContext(), list) }
                            postsRecyclerView.adapter = postsAdapter
                        }
                    }
                }
                RequestStatus.EXCEPTION -> {
                    snackBar(requireView(), it.message ?: getString(R.string.some_error_occurred)).show()
                }
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
                    if (this::dialog.isInitialized) dialog.hide()
                    userViewModel.currentUserLiveData.value?.data?.let { user ->
                        val currentUser: User = user
                        currentUser.profilePic = it.data.toString()
                        viewModel.updateUserDetails(currentUser, Firebase.auth.uid!!)
                    }
                }
                RequestStatus.EXCEPTION -> {
                    if (this::dialog.isInitialized) dialog.hide()
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
                    if (this::dialog.isInitialized) dialog.hide()
                    userViewModel.currentUserLiveData.value?.data?.let { user ->
                        val currentUser: User = user
                        currentUser.coverPic = it.data.toString()
                        viewModel.updateUserDetails(currentUser, Firebase.auth.uid!!)
                    }
                }
                RequestStatus.EXCEPTION -> {
                    if (this::dialog.isInitialized) dialog.hide()
                    snackBar(requireView(), it.message ?: getString(R.string.some_error_occurred)).show()
                }
            }
        }
    }

    private fun handleUpdateUserDetails() {
        viewModel.updateUserDetailsLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                RequestStatus.LOADING -> {
                }
                RequestStatus.SUCCESS -> {
                    userViewModel.currentUserLiveData.value = Resource.success(it.data)
                    it.data?.coverPic?.let { coverPic ->
                        Picasso.get().load(coverPic).placeholder(R.drawable.ic_default_user).into(binding.coverImage)
                    }
                    it.data?.profilePic?.let { profilePic ->
                        Picasso.get().load(profilePic).placeholder(R.drawable.ic_default_user).into(binding.profileImage)
                    }
                }
                RequestStatus.EXCEPTION -> {
                    snackBar(requireView(), it.message ?: getString(R.string.some_error_occurred)).show()
                }
            }
        }
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null && data.data != null) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}