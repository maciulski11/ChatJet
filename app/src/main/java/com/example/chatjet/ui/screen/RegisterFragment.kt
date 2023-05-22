package com.example.chatjet.ui.screen

import android.util.Patterns
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.chatjet.R
import com.example.chatjet.base.BaseFragment
import com.example.chatjet.services.utils.Utilities
import com.example.chatjet.view_model.MainViewModel
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.fragment_register.fullNameET
import kotlinx.android.synthetic.main.fragment_register.phoneNumberET

class RegisterFragment : BaseFragment() {
    override val layout: Int = R.layout.fragment_register

    private val mainViewModel: MainViewModel by activityViewModels()

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

    /**
     * the input is not valid if...
     * ...the username/password is empty
     * ...the username is already taken
     * ...the confirmed password is not the same as the real password
     * ...the password contains less than 2 digits
     */
    fun validateRegistrationInput(
        fullName: String,
        password: String,
        confirmedPassword: String,
        phoneNumber: String,
        email: String
    ): Boolean {

        if (fullName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || password.isEmpty() || confirmedPassword.isEmpty()) {

            Utilities.showToast(
                "All fields must be completed!",
                R.color.red,
                R.drawable.ic_baseline_remove_circle_outline_24,
                Toast.LENGTH_SHORT
            )
            return false
        }

        // Checks the email according to the pattern
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            Utilities.showToast(
                "Email is not valid!",
                R.color.red,
                R.drawable.ic_baseline_remove_circle_outline_24,
                Toast.LENGTH_SHORT
            )
            return false
        }

        if (password != confirmedPassword) {

            Utilities.showToast(
                "The passwords are different!",
                R.color.red,
                R.drawable.ic_baseline_remove_circle_outline_24,
                Toast.LENGTH_SHORT
            )
            return false
        }

        if (password.count { it.isDigit() } < 1) {

            Utilities.showToast(
                "Password must have at least 1 digit!",
                R.color.red,
                R.drawable.ic_baseline_remove_circle_outline_24,
                Toast.LENGTH_SHORT
            )
            return false
        }

        if (password.none { it.isUpperCase() }) {

            Utilities.showToast(
                "Password must have at least 1 capital letter!",
                R.color.red,
                R.drawable.ic_baseline_remove_circle_outline_24,
                Toast.LENGTH_SHORT
            )
            return false
        }

        if (password.length < 8) {

            Utilities.showToast(
                "Password must have at least 8 characters!",
                R.color.red,
                R.drawable.ic_baseline_remove_circle_outline_24,
                Toast.LENGTH_SHORT
            )
            return false
        }

        if (phoneNumber.length < 9) {

            Utilities.showToast(
                "Your phone number should have 9 digits!",
                R.color.red,
                R.drawable.ic_baseline_remove_circle_outline_24,
                Toast.LENGTH_SHORT
            )
            return false
        }

        Utilities.showToast(
            "Registered successfully!\nPlease check your email for verification.",
            R.color.green,
            R.drawable.ic_baseline_check_circle_outline_24,
            Toast.LENGTH_LONG
        )

        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)

        mainViewModel.registerUser(email, fullName, phoneNumber.toInt(), password)

        return true
    }

    override fun unsubscribeUi() {

    }
}