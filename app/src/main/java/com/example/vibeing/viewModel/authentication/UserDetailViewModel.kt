package com.example.vibeing.viewModel.authentication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vibeing.models.User
import com.example.vibeing.repository.AuthenticationRepository
import com.example.vibeing.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(private val repository: AuthenticationRepository) : ViewModel() {
    var createUserLiveData = MutableLiveData<Resource<Boolean>>()
    fun createUserProfile(user: User) {
        createUserLiveData.value = Resource.loading(null)
        viewModelScope.launch {
            val result = repository.createUserProfile(user)
            createUserLiveData.value = result
        }
    }
}