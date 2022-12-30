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
class SignupViewModel @Inject constructor(private val repository: AuthenticationRepository) : ViewModel() {
    var signupUserLiveData = MutableLiveData<Resource<FirebaseUser>>()
    fun signupUser(email: String, password: String) {
        signupUserLiveData.value = Resource.loading(null)
        viewModelScope.launch {
            val result = repository.signupUser(email, password)
            signupUserLiveData.value = result
        }
    }
}