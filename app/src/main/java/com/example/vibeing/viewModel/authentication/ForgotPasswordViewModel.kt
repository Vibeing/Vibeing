package com.example.vibeing.viewModel.authentication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vibeing.repository.AuthenticationRepository
import com.example.vibeing.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(private val repository: AuthenticationRepository) : ViewModel() {
    var sendResetPasswordLinkLiveData = MutableLiveData<Resource<Boolean>>()
    fun sendResetPasswordLink(email: String) {
        sendResetPasswordLinkLiveData.value = Resource.loading(null)
        viewModelScope.launch {
            val result = repository.sendResetPasswordLink(email)
            sendResetPasswordLinkLiveData.value = result
        }
    }
}