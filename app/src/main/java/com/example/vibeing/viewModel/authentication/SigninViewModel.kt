package com.example.vibeing.viewModel.authentication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vibeing.repository.AuthenticationRepository
import com.example.vibeing.utils.Resource
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SigninViewModel @Inject constructor(private val repository: AuthenticationRepository) : ViewModel() {
    var signinUserLiveData = MutableLiveData<Resource<FirebaseUser>>()
    fun signinUser(email: String, password: String) {
        signinUserLiveData.value = Resource.loading(null)
        viewModelScope.launch {
            val result = repository.signinUser(email, password)
            signinUserLiveData.value = result
        }
    }

    var checkUserLiveData = MutableLiveData<Resource<Boolean>>()
    fun checkCurrentUser(userId: String) {
        checkUserLiveData.value = Resource.loading(null)
        viewModelScope.launch {
            val result = repository.getCurrentUser(userId)
            checkUserLiveData.value = result
        }
    }
}