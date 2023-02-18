package com.example.vibeing.viewModel.home

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vibeing.models.Post
import com.example.vibeing.models.User
import com.example.vibeing.repository.HomeRepository
import com.example.vibeing.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: HomeRepository) : ViewModel() {

    var updateUserDetailsLiveData = MutableLiveData<Resource<User>>()
    fun updateUserDetails(user: User, uid: String) {
        viewModelScope.launch {
            updateUserDetailsLiveData.value = repository.updateUserDetails(user, uid)
        }
    }

    var getUserPostsLiveData = MutableLiveData<Resource<ArrayList<Post>>>()
    fun getUserPosts(uid: String) {
        viewModelScope.launch {
            getUserPostsLiveData.value = repository.getUserPosts(uid)
        }
    }

    var addProfileImageToServerLiveData = MutableLiveData<Resource<String>>()
    fun addProfileImageToStorage(uri: Uri, uid: String) {
        addProfileImageToServerLiveData.value = Resource.loading(null)
        viewModelScope.launch {
            addProfileImageToServerLiveData.value = repository.getProfileImgUrlFromStorage(uri, uid)
        }
    }

    var addCoverImageToServerLiveData = MutableLiveData<Resource<String>>()
    fun addCoverImageToStorage(uri: Uri, uid: String) {
        addCoverImageToServerLiveData.value = Resource.loading(null)
        viewModelScope.launch {
            addCoverImageToServerLiveData.value = repository.getCoverImgUrlFromStorage(uri, uid)
        }
    }
}