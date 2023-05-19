package com.example.chatjet.ui.screen

import android.util.Patterns
import android.widget.Toast
import com.example.chatjet.R
import com.example.chatjet.base.BaseFragment
import com.example.chatjet.services.utils.Utilities
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.fragment_register.fullNameET
import kotlinx.android.synthetic.main.fragment_register.phoneNumberET

class RegisterFragment: BaseFragment() {
    override val layout: Int = R.layout.fragment_register

    override fun subscribeUi() {

        registerButton.setOnClickListener {
            validateRegistrationInput(
                fullNameET.text.toString(),
                passwordET.text.toString(),
                confirmPasswordET.text.toString(),
                phoneNumberET.text.toString(),
                emailET.text.toString(),
                )
        }

    }

    private val existingUsers = listOf("Peter", "Carl")

    private fun showToast(message: String, backgroundToastColor: Int, iconResId: Int) {
        context?.let {
            Utilities.customToast(
                message,
                iconResId,
                R.color.white,
                backgroundToastColor,
                Toast.LENGTH_SHORT
            )
        }
    }

    /**
     * the input is not valid if...
     * ...the username/password is empty
     * ...the username is already taken
     * ...the confirmed password is not the same as the real password
     * ...the password contains less than 2 digits
     */
    fun validateRegistrationInput(
        username: String,
        password: String,
        confirmedPassword: String,
        phoneNumber: String,
        email: String
        ): Boolean {

        if(username.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || password.isEmpty()) {

            showToast("All fields must be completed!", R.color.red, R.drawable.ic_baseline_remove_circle_outline_24)
            return false
        }

        // Checks the email according to the pattern
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            showToast("Email is not valid!", R.color.red, R.drawable.ic_baseline_remove_circle_outline_24)
            return false
        }

        if(password != confirmedPassword) {

            showToast("The passwords are different!", R.color.red, R.drawable.ic_baseline_remove_circle_outline_24)
            return false
        }

        if(password.count { it.isDigit() } < 1) {

            showToast("Password must have at least 1 digit!", R.color.red, R.drawable.ic_baseline_remove_circle_outline_24)
            return false
        }

        if(password.none { it.isUpperCase() }) {

            showToast("Password must have at least 1 capital letter!", R.color.red, R.drawable.ic_baseline_remove_circle_outline_24)
            return false
        }

        if(password.length < 8) {

            showToast("Password must have at least 8 characters!", R.color.red, R.drawable.ic_baseline_remove_circle_outline_24)
            return false
        }

        if(phoneNumber.length < 9) {

            showToast("Your phone number should have 9 digits!", R.color.red, R.drawable.ic_baseline_remove_circle_outline_24)
            return false
        }

        showToast("Success!", R.color.green, R.drawable.ic_baseline_check_circle_outline_24)
        return true
    }

    override fun unsubscribeUi() {

    }
}