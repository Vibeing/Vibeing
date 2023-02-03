package com.example.vibeing.ui.home

import android.app.Activity
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.example.vibeing.R
import com.example.vibeing.adapters.home.DropdownAdapter
import com.example.vibeing.databinding.FragmentAddPostBinding
import com.example.vibeing.models.Post
import com.example.vibeing.utils.FunctionUtils.focusScreen
import com.example.vibeing.utils.FunctionUtils.hideKeyboard
import com.example.vibeing.utils.FunctionUtils.openGallery
import com.example.vibeing.utils.FunctionUtils.setUpDialog
import com.example.vibeing.utils.FunctionUtils.snackBar
import com.example.vibeing.utils.RequestStatus
import com.example.vibeing.viewModel.home.AddPostViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AddPostFragment : Fragment() {
    private var _binding: FragmentAddPostBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AddPostViewModel>()
    private var postImageUrl: Uri? = null
    private lateinit var dialog: Dialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddPostBinding.inflate(layoutInflater)
        focusScreen(binding.root)
        allowEnterTransitionOverlap = true
        allowReturnTransitionOverlap = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpClickListener()
        setUpVisibilitySpinner()
        handleAddPostLiveDataStatusChange()
        handleAddPostImageToStorage()
        handleAddPostBtnStatus()
    }

    private fun setUpClickListener() {
        with(binding) {
            addPostBtn.setOnClickListener {
                addPost()
            }
            captionEdit.addTextChangedListener(textWatcher)
            galleryImg.setOnClickListener {
                openGallery(resultLauncher)
            }
            clearPostImg.setOnClickListener { clearPostImage() }
        }
    }

    private fun setUpVisibilitySpinner() {
        val spinnerList = ArrayList<Pair<Int, String>>()
        spinnerList.add(Pair(R.drawable.ic_everyone, getString(R.string.everyone)))
        spinnerList.add(Pair(R.drawable.ic_friends, getString(R.string.friends)))
        val dropdownAdapter = DropdownAdapter(requireContext(), spinnerList)
        with(binding) {
            visibilitySpin.adapter = dropdownAdapter
        }
    }

    private fun handleAddPostLiveDataStatusChange() {
        viewModel.addPostLiveData.observe(viewLifecycleOwner) {
            with(binding) {
                when (it.status) {
                    RequestStatus.LOADING -> {
                        dialog.show()
                        addPostBtn.isClickable = false
                    }
                    RequestStatus.SUCCESS -> {
                        addPostBtn.isClickable = true
                        dialog.dismiss()
                        snackBar(requireView(), getString(R.string.post_uploaded_successfully)).show()
                        captionEdit.text.clear()
                        postImageUrl = null
                        clearPostImg.hide()
                        postImg.visibility = View.GONE
                        Navigation.findNavController(requireView()).popBackStack()
                    }
                    RequestStatus.EXCEPTION -> {
                        addPostBtn.isClickable = true
                        dialog.dismiss()
                        snackBar(requireView(), it.message ?: getString(R.string.some_error_occurred)).show()
                    }
                }
            }
        }
    }

    private fun handleAddPostImageToStorage() {
        viewModel.addPostImageToServerLiveData.observe(viewLifecycleOwner) {
            with(binding) {
                when (it.status) {
                    RequestStatus.LOADING -> {
                        dialog.show()
                        addPostBtn.isClickable = false
                    }
                    RequestStatus.SUCCESS -> {
                        dialog.hide()
                        addPostBtn.isClickable = true
                        val post = Post(it.data.toString(), binding.captionEdit.text.toString(), Firebase.auth.uid!!, binding.visibilitySpin.selectedItemPosition, Date().time)
                        viewModel.addPost(post)
                        //Firebase.auth.uid?.let { it1 -> viewModel.checkCurrentUser(it1) }
                    }
                    RequestStatus.EXCEPTION -> {
                        dialog.hide()
                        addPostBtn.isClickable = true
                        snackBar(requireView(), it.message ?: getString(R.string.some_error_occurred)).show()
                    }
                }
            }
        }
    }

    private fun handleAddPostBtnStatus() {
        viewModel.addPostButtonStateLiveData.observe(viewLifecycleOwner) {
            binding.addPostBtn.isEnabled = it
        }
    }

    private fun clearPostImage() {
        postImageUrl = null
        with(binding) {
            viewModel.addPostButtonStateLiveData.value = captionEdit.text.toString().isNotBlank() || false
            postImg.setImageURI(null)
            postImg.visibility = View.GONE
            clearPostImg.visibility = View.GONE
        }
    }

    private fun addPost() {
        hideKeyboard(requireContext(), requireView())
        dialog = setUpDialog(getString(R.string.uploading_your_post), requireContext())
        with(binding) {
            if (postImageUrl != null) {
                viewModel.addPostImageToStorage(postImageUrl!!, Firebase.auth.uid!!)
                return
            }
            val caption = captionEdit.text.toString()
            val post = Post("", caption, Firebase.auth.uid!!, visibilitySpin.selectedItemPosition, Date().time)
            viewModel.addPost(post)
        }
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null && data.data != null) {
                postImageUrl = data.data
                with(binding) {
                    postImg.setImageURI(postImageUrl!!)
                    viewModel.addPostButtonStateLiveData.value = true
                    if (postImg.visibility != View.VISIBLE)
                        postImg.visibility = View.VISIBLE
                    if (clearPostImg.visibility != View.VISIBLE)
                        clearPostImg.visibility = View.VISIBLE
                }
            }
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            viewModel.addPostButtonStateLiveData.value = (p0.toString().isNotBlank()) || (postImageUrl != null)
        }

        override fun afterTextChanged(p0: Editable?) {
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}