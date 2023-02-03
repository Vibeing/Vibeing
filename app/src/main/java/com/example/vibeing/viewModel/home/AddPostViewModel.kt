package com.example.vibeing.viewModel.home

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vibeing.models.Post
import com.example.vibeing.repository.HomeRepository
import com.example.vibeing.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPostViewModel @Inject constructor(private val repository: HomeRepository) : ViewModel() {
    var addPostLiveData = MutableLiveData<Resource<Boolean>>()
    var addPostButtonStateLiveData = MutableLiveData<Boolean>()
    fun addPost(post: Post) {
        addPostLiveData.value = Resource.loading(null)
        viewModelScope.launch {
            val result = repository.addPost(post)
            addPostLiveData.value = result
        }
    }

    var addPostImageToServerLiveData = MutableLiveData<Resource<String>>()
    fun addPostImageToStorage(uri: Uri, uid: String) {
        addPostImageToServerLiveData.value = Resource.loading(null)
        viewModelScope.launch {
            val result = repository.getPostImgUrlFromStorage(uri, uid)
            addPostImageToServerLiveData.value = result
        }
    }
}