package com.example.vibeing.repository

import com.example.vibeing.models.User
import com.example.vibeing.utils.Constants.KEY_USER
import com.example.vibeing.utils.FunctionUtils.getException
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
            getException(exception, null)
        }
    }

    suspend fun createUserProfile(user: User): Resource<Boolean> {
        return if (Firebase.auth.uid != null) {
            try {
                Firebase.firestore.collection(KEY_USER).document(Firebase.auth.uid!!).set(user).await()
                Resource.success(true)
            } catch (exception: Exception) {
                getException(exception, false)
            }
        } else {
            Resource.error(false, "Some error occurred")
        }
    }

    suspend fun signinUser(email: String, password: String): Resource<FirebaseUser> {
        return try {
            val result = Firebase.auth.signInWithEmailAndPassword(email, password).await()
            Resource.success(result.user)
        } catch (exception: Exception) {
            getException(exception, null)
        }
    }

    suspend fun sendResetPasswordLink(email: String): Resource<Boolean> {
        return try {
            Firebase.auth.sendPasswordResetEmail(email).await()
            Resource.success(true)
        } catch (exception: Exception) {
            getException(exception, false)
        }
    }

    suspend fun getCurrentUser(uid: String): Resource<Boolean> {
        return try {
            val result = Firebase.firestore.collection(KEY_USER).document(uid).get().await()
            if (!result.exists()) {
                return Resource.success(false)
            } else Resource.success(true)
        } catch (exception: Exception) {
            getException(exception, false)
        }
    }
}