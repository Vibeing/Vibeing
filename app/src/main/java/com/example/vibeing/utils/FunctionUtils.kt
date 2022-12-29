package com.example.vibeing.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import androidx.navigation.Navigation
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo

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
    fun navigate(view: View,id:Int){
        Navigation.findNavController(view).navigate(id)
    }
}