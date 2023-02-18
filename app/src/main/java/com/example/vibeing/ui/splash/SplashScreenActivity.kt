package com.example.vibeing.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.daimajia.androidanimations.library.Techniques
import com.example.vibeing.databinding.ActivitySplashScreenBinding
import com.example.vibeing.ui.authentication.AuthenticationActivity
import com.example.vibeing.ui.home.HomeActivity
import com.example.vibeing.utils.FunctionUtils.animateView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideStatusBar()
        animateView(binding.appNameTxt, duration = 1000, techniques = Techniques.FadeIn)
        delayScreen()
    }

    private fun delayScreen() {
        Handler(Looper.getMainLooper()).postDelayed({
            checkCurrentUser()
        }, 1500)
    }

    @Suppress("DEPRECATION")
    private fun hideStatusBar() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        supportActionBar?.hide()
    }

    private fun checkCurrentUser() {
        if (Firebase.auth.currentUser != null) {
            startActivity(Intent(this, HomeActivity::class.java))
        } else {
            startActivity(Intent(this, AuthenticationActivity::class.java))
        }
        finish()
    }
}