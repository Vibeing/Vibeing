package com.example.vibeing.viewModel.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vibeing.models.User
import com.example.vibeing.repository.HomeRepository
import com.example.vibeing.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetCurrentViewModel @Inject constructor(private val repository: HomeRepository) : ViewModel() {
    var currentUserLiveData = MutableLiveData<Resource<User>>()
    fun getCurrentUser(uid: String) {
        currentUserLiveData.value = Resource.loading(null)
        viewModelScope.launch {
            val result = repository.getCurrentUser(uid)
            currentUserLiveData.value = result
        }
    }
}