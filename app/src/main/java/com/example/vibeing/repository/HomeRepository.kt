package com.example.vibeing.repository

import android.net.Uri
import android.util.Log
import com.example.vibeing.models.Post
import com.example.vibeing.models.User
import com.example.vibeing.utils.Constants.KEY_POST
import com.example.vibeing.utils.Constants.KEY_USER
import com.example.vibeing.utils.FunctionUtils.getException
import com.example.vibeing.utils.Resource
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

class HomeRepository @Inject constructor() {
    suspend fun addPost(post: Post): Resource<Boolean> {
        return try {
            Firebase.firestore.collection(KEY_POST).add(post).await()
            Resource.success(true)
        } catch (exception: Exception) {
            getException(exception, false)
        }
    }

    suspend fun getPostImgUrlFromStorage(url: Uri, uid: String): Resource<String> {
        return try {
            val reference = FirebaseStorage.getInstance().reference.child(KEY_POST).child(uid).child(Date().time.toString())
            reference.putFile(url).await()
            val downloadImageUrlResult = reference.downloadUrl.await()
            Resource.success(downloadImageUrlResult.toString())
        } catch (exception: Exception) {
            getException(exception, "")
        }
    }

    suspend fun getCurrentUser(uid: String): Resource<User> {
        return try {
            val result = Firebase.firestore.collection(KEY_USER).document(uid).get().await()
            if (!result.exists()) {
                return Resource.success(null)
            } else Resource.success(result.toObject(User::class.java))
        } catch (exception: Exception) {
            Log.e("abc",exception.localizedMessage)
            getException(exception, null)
        }
    }
}
