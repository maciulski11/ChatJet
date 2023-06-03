package com.example.chatjet.ui.screen

import android.util.Patterns
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.chatjet.R
import com.example.chatjet.base.BaseFragment
import com.example.chatjet.services.utils.ToastUtils
import com.example.chatjet.view_model.MainViewModel
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.fragment_register.fullNameET
import kotlinx.android.synthetic.main.fragment_register.phoneNumberET

class RegisterFragment : BaseFragment() {
    override val layout: Int = R.layout.fragment_register

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun subscribeUi() {

        returnBT.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

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

            ToastUtils.showToast(
                "All fields must be completed!",
                R.drawable.ic_baseline_remove_circle_outline_24,
                R.color.red,
                Toast.LENGTH_SHORT
            )
            return false
        }

        // Checks the email according to the pattern
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            ToastUtils.showToast(
                "Email is not valid!",
                R.drawable.ic_baseline_remove_circle_outline_24,
                R.color.red,
                Toast.LENGTH_SHORT
            )
            return false
        }

        if (password != confirmedPassword) {

            ToastUtils.showToast(
                "The passwords are different!",
                R.drawable.ic_baseline_remove_circle_outline_24,
                R.color.red,
                Toast.LENGTH_SHORT
            )
            return false
        }

        if (password.count { it.isDigit() } < 1) {

            ToastUtils.showToast(
                "Password must have at least 1 digit!",
                R.drawable.ic_baseline_remove_circle_outline_24,
                R.color.red,
                Toast.LENGTH_SHORT
            )
            return false
        }

        if (password.none { it.isUpperCase() }) {

            ToastUtils.showToast(
                "Password must have at least 1 capital letter!",
                R.drawable.ic_baseline_remove_circle_outline_24,
                R.color.red,
                Toast.LENGTH_SHORT
            )
            return false
        }

        if (password.length < 8) {

            ToastUtils.showToast(
                "Password must have at least 8 characters!",
                R.drawable.ic_baseline_remove_circle_outline_24,
                R.color.red,
                Toast.LENGTH_SHORT
            )
            return false
        }

        if (phoneNumber.length < 9) {

            ToastUtils.showToast(
                "Your phone number should have 9 digits!",
                R.drawable.ic_baseline_remove_circle_outline_24,
                R.color.red,
                Toast.LENGTH_SHORT
            )
            return false
        }

        ToastUtils.showToast(
            "Registered successfully!\nPlease check your email for verification.",
            R.drawable.ic_baseline_check_circle_outline_24,
            R.color.green,
            Toast.LENGTH_LONG
        )

        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)

        mainViewModel.registerUser(email, fullName, phoneNumber.toInt(), password)

        return true
    }

    override fun unsubscribeUi() {

    }
}