package com.example.vibeing.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.Toast
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
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

    fun navigate(view: View, action: NavDirections? = null, id: Int? = null) {
        if (action == null && id != null)
            Navigation.findNavController(view).navigate(id)
        else if (id == null && action != null)
            Navigation.findNavController(view).navigate(action)
    }

    fun toast(context: Context, msg: String, time: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, msg, time).show()
    }

    fun snackbar(view: View, msg: String, time: Int = Snackbar.LENGTH_SHORT): Snackbar {
        return Snackbar.make(view, msg, time)
    }

}