package com.example.vibeing.ui.authentication

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.vibeing.R
import com.example.vibeing.databinding.ActivityAuthenticationBinding
import com.example.vibeing.utils.Constants.BACK_BTN_TIME_OUT
import dagger.hilt.android.AndroidEntryPoint


@Suppress("DEPRECATION")
@AndroidEntryPoint
class AuthenticationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthenticationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideStatusBar()
    }

    private fun hideStatusBar() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        supportActionBar?.hide()
    }

    private var doubleBackToExitPressedOnce = false

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        //Checking for fragment count on backstack
        if ((supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.backStackEntryCount ?: 0) > 0) {
            super.onBackPressed()
        } else if (!doubleBackToExitPressedOnce) {
            doubleBackToExitPressedOnce = true
            Toast.makeText(this, getString(R.string.please_click_back_to_exit_app), Toast.LENGTH_SHORT).show()
            Handler().postDelayed({ doubleBackToExitPressedOnce = false }, BACK_BTN_TIME_OUT)
        } else {
            super.onBackPressed()
            return
        }
    }
}