package com.example.vibeing.utils

import android.app.ActionBar
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.vibeing.R
import com.example.vibeing.databinding.LayoutProgressDialogBinding
import com.example.vibeing.utils.Constants.TYPE_IMAGE
import com.google.android.material.snackbar.Snackbar


object FunctionUtils {
    fun animateView(view: View, duration: Long = 500, repeat: Int = 0, techniques: Techniques = Techniques.Shake) {
        YoYo.with(techniques).duration(duration).repeat(repeat).playOn(view)
    }

    @Suppress("DEPRECATION")
    fun vibrateDevice(context: Context, time: Long = 300) {
        if (Build.VERSION.SDK_INT >= 26) {
            (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
                .vibrate(VibrationEffect.createOneShot(time, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(time)
        }
    }

    fun getMonthNameFromMonthNumber(monthNumber: Int): String {
        val monthList = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        return if (monthNumber in 1..12)
            monthList[monthNumber]
        else "error"
    }

    fun focusScreen(view: View) {
        view.setOnApplyWindowInsetsListener { _, windowInsets ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val imeHeight = windowInsets.getInsets(WindowInsets.Type.ime()).bottom
                view.setPadding(0, 0, 0, imeHeight)
            }
            windowInsets
        }
    }

    fun hideKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun navigate(view: View, action: NavDirections? = null, id: Int? = null) {
        if (action == null && id != null)
            Navigation.findNavController(view).navigate(id)
        else if (id == null && action != null)
            Navigation.findNavController(view).navigate(action)
    }

    fun toast(context: Context, msg: String, time: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, msg, time).show()
    }

    fun snackBar(view: View, msg: String, time: Int = Snackbar.LENGTH_SHORT): Snackbar {
        return Snackbar.make(view, msg, time)
    }

    fun <T> getException(exception: Exception, data: T?): Resource<T> {
        exception.printStackTrace()
        var message = exception.localizedMessage
        if (message?.contains(":") == true)
            message = message.substringAfter(":")
        return Resource.error(data, message ?: "Some error occurred")
    }

    fun openGallery(resultLauncher: ActivityResultLauncher<Intent>) {
        Intent().apply {
            this.action = Intent.ACTION_GET_CONTENT
            this.type = TYPE_IMAGE
            resultLauncher.launch(this)
        }
    }

    fun setUpDialog(message: String, context: Context): Dialog {
        val dialog = Dialog(context, R.style.CustomDialogTheme).apply {
            this.requestWindowFeature(Window.FEATURE_NO_TITLE)
            this.setCancelable(false)
            val dialogBinding = LayoutProgressDialogBinding.inflate(layoutInflater)
            this.setContentView(dialogBinding.root)
            val width = (context.resources.displayMetrics.widthPixels * 0.80).toInt()
            this.window?.setLayout(width, ActionBar.LayoutParams.WRAP_CONTENT)
            dialogBinding.dialogMessageTxt.text = message
        }
        return dialog
    }
}