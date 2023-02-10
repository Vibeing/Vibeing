package com.example.vibeing.repository

import android.net.Uri
import android.util.Log
import com.example.vibeing.models.Post
import com.example.vibeing.models.User
<<<<<<< HEAD
import com.example.vibeing.utils.Constants.KEY_COVER_PIC
import com.example.vibeing.utils.Constants.KEY_POST
import com.example.vibeing.utils.Constants.KEY_PROFILE_PIC
=======
import com.example.vibeing.utils.Constants.KEY_POST
>>>>>>> origin/development
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

    suspend fun updateUserDetails(user: User, uid: String): Resource<User> {
        return try {
            Firebase.firestore.collection(KEY_USER).document(uid).set(user).await()
            Resource.success(user)
        } catch (exception: Exception) {
            getException(exception, null)
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

<<<<<<< HEAD
    suspend fun getProfileImgUrlFromStorage(url: Uri, uid: String): Resource<String> {
        return try {
            val reference = FirebaseStorage.getInstance().reference.child(KEY_PROFILE_PIC).child(uid)
            reference.putFile(url).await()
            val downloadImageUrlResult = reference.downloadUrl.await()
            Resource.success(downloadImageUrlResult.toString())
        } catch (exception: Exception) {
            getException(exception, "")
        }
    }

    suspend fun getCoverImgUrlFromStorage(url: Uri, uid: String): Resource<String> {
        return try {
            val reference = FirebaseStorage.getInstance().reference.child(KEY_COVER_PIC).child(uid)
            reference.putFile(url).await()
            val downloadImageUrlResult = reference.downloadUrl.await()
            Resource.success(downloadImageUrlResult.toString())
        } catch (exception: Exception) {
            getException(exception, "")
        }
    }

=======
>>>>>>> origin/development
    suspend fun getCurrentUser(uid: String): Resource<User> {
        return try {
            val result = Firebase.firestore.collection(KEY_USER).document(uid).get().await()
            if (!result.exists()) {
                return Resource.success(null)
            } else Resource.success(result.toObject(User::class.java))
        } catch (exception: Exception) {
<<<<<<< HEAD
            exception.localizedMessage?.let { Log.e("abc", it) }
=======
            Log.e("abc",exception.localizedMessage)
>>>>>>> origin/development
            getException(exception, null)
        }
    }
}
