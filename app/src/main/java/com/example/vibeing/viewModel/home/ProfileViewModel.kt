package com.example.vibeing.viewModel.home

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vibeing.repository.HomeRepository
import com.example.vibeing.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: HomeRepository) : ViewModel() {

    fun updateProfileOrCoverImage(url: Uri, uid: String, key: String) {
        viewModelScope.launch {
            repository.updateProfileOrCoverPhoto(url, uid, key)
        }
    }

    var addProfileImageToServerLiveData = MutableLiveData<Resource<String>>()
    fun addProfileImageToStorage(uri: Uri, uid: String) {
        addProfileImageToServerLiveData.value = Resource.loading(null)
        viewModelScope.launch {
            val result = repository.getProfileImgUrlFromStorage(uri, uid)
            addProfileImageToServerLiveData.value = result
        }
    }

    var addCoverImageToServerLiveData = MutableLiveData<Resource<String>>()
    fun addCoverImageToStorage(uri: Uri, uid: String) {
        addCoverImageToServerLiveData.value = Resource.loading(null)
        viewModelScope.launch {
            val result = repository.getCoverImgUrlFromStorage(uri, uid)
            addCoverImageToServerLiveData.value = result
        }
    }
}