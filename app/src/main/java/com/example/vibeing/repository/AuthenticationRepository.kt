package com.example.vibeing.repository

import com.example.vibeing.models.User
import com.example.vibeing.utils.Constants.USER_KEY
import com.example.vibeing.utils.Resource
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthenticationRepository @Inject constructor() {
    suspend fun signupUser(email: String, password: String): Resource<FirebaseUser> {
        return try {
            val result = Firebase.auth.createUserWithEmailAndPassword(email, password).await()
            Resource.success(result.user!!)
        } catch (exception: Exception) {
            exception.printStackTrace()
            var message = exception.localizedMessage
            if (message?.contains(":") == true)
                message = message.substringAfter(":")
            Resource.error(null, message ?: "Some error occurred")
        }
    }

    suspend fun createUserProfile(user: User): Resource<Boolean> {
        return try {
            Firebase.firestore.collection(USER_KEY).document().set(user).await()
            Resource.success(true)
        } catch (exception: Exception) {
            exception.printStackTrace()
            var message = exception.localizedMessage
            if (message?.contains(":") == true)
                message = message.substringAfter(":")
            Resource.error(false, message ?: "Some error occurred")
        }
    }
}