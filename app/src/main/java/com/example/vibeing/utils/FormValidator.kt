package com.example.vibeing.utils

import android.content.Context
import com.example.vibeing.R
import com.example.vibeing.utils.Constants.EMAIL_REGEX
import java.util.regex.Pattern

object FormValidator {


    /**
     * the email is not validated if...
     * ...email is empty
     * ...email regex do not match
     */
    fun validateEmail(context: Context, email: String): String {
        if (email.isBlank())
            return context.getString(R.string.please_enter_email_address)
        val pattern = Pattern.compile(EMAIL_REGEX)
        val matcher = pattern.matcher(email)
        if (!matcher.matches())
            return context.getString(R.string.invalid_email_address)
        return ""
    }

    /**
     * the password is not validated if...
     * ...password is empty
     * ...password less than 6 chars
     */
    fun validatePassword(context: Context, password: String): String {
        if (password.isBlank())
            return context.getString(R.string.please_enter_password)
        if (password.length < 6)
            return context.getString(R.string.password_must_be_atleast_six_chars)
        return ""
    }

    /**
     * the confirm password is not validated if...
     * ...password and confirm password do not match
     */
    fun validateConfirmPassword(password: String, confirmPassword: String): Boolean {
        if (password != confirmPassword)
            return false
        return true
    }

    /**
     * the name is not validated if...
     * ...name is empty
     * ...name less than 2 chars
     */
    fun validateName(context: Context, name: String): String {
        if (name.isBlank())
            return context.getString(R.string.please_enter_name)
        if (name.length < 2)
            return context.getString(R.string.name_must_be_atleast_two_chars)
        return ""
    }

    /**
     * the dob is not validated if...
     * ...dob is empty
     */
    fun validateDateOfBirth(dob: String): Boolean {
        if (dob.isBlank())
            return false
        return true
    }

    /**
     * the gender is not validated if...
     * ...gender is empty
     * ...gender not in gender list
     */
    fun validateGender(context: Context, gender: String): String {
        val genderList = listOf(
            context.getString(R.string.male), context.getString(R.string.female), context.getString(
                R.string.other
            )
        )
        if (gender.isBlank())
            return context.getString(R.string.please_select_your_gender)
        if (gender !in genderList)
            return context.getString(R.string.incorrect_gender_type)
        return ""

    }
}