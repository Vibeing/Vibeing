package com.example.vibeing.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.vibeing.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        Firebase.auth.signOut()
    }
}