package com.example.vibeing.utils

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import org.junit.Test

internal class FormValidatorTest {
    // testing email
    @Test
    fun emptyEmail_ReturnFalse() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val result = FormValidator.validateEmail(context, "")
        assertThat(result).isNotEmpty()
    }

    @Test
    fun wrongEmail_ReturnFalse() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val result = FormValidator.validateEmail(context, "abc@123")
        assertThat(result).isNotEmpty()
    }

    @Test
    fun correctEmail_ReturnTrue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val result = FormValidator.validateEmail(context, "abc@gmail.com")
        assertThat(result).isEmpty()
    }

    //testing password
    @Test
    fun emptyPassword_ReturnFalse() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val result = FormValidator.validatePassword(context, "")
        assertThat(result).isNotEmpty()
    }

    @Test
    fun passwordLessThan6_ReturnFalse() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val result = FormValidator.validatePassword(context, "123")
        assertThat(result).isNotEmpty()
    }


    @Test
    fun correctPassword_ReturnTrue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val result = FormValidator.validatePassword(context, "123456")
        assertThat(result).isEmpty()
    }

    //testing matching password and confirm password
    @Test
    fun confirmPassAndCurrentPassDoNotMatch_ReturnFalse() {
        val result = FormValidator.validateConfirmPassword("123456", "123456789")
        assertThat(result).isFalse()
    }

    @Test
    fun confirmPassAndCurrentPassMatch_ReturnTrue() {
        val result = FormValidator.validateConfirmPassword("123456", "123456")
        assertThat(result).isTrue()
    }

    //testing name
    @Test
    fun emptyName_ReturnFalse() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val result = FormValidator.validateName(context, "")
        assertThat(result).isNotEmpty()
    }

    @Test
    fun nameLessThan2_ReturnFalse() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val result = FormValidator.validateName(context, "A")
        assertThat(result).isNotEmpty()
    }

    @Test
    fun validName_ReturnTrue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val result = FormValidator.validateName(context, "AK")
        assertThat(result).isEmpty()
    }

    //testing dob
    @Test
    fun emptyDOB_ReturnFalse() {
        val result = FormValidator.validateDateOfBirth("")
        assertThat(result).isFalse()
    }

    @Test
    fun validDOB_ReturnTrue() {
        val result = FormValidator.validateDateOfBirth("16 Dec 2022")
        assertThat(result).isTrue()
    }

    //testing gender
    @Test
    fun emptyGender_ReturnFalse() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val result = FormValidator.validateGender(context, "")
        assertThat(result).isNotEmpty()
    }

    @Test
    fun wrongGender_ReturnFalse() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val result = FormValidator.validateGender(context, "prefer not to specify")
        assertThat(result).isNotEmpty()
    }

    @Test
    fun validGender_ReturnTrue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val result = FormValidator.validateGender(context, "Male")
        assertThat(result).isEmpty()
    }
}