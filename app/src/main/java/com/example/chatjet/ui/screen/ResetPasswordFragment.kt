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
import kotlinx.android.synthetic.main.fragment_reset_password.*

class ResetPasswordFragment: BaseFragment() {
    override val layout: Int = R.layout.fragment_reset_password

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun subscribeUi() {

        returnButton.setOnClickListener {
            findNavController().navigate(R.id.action_resetPasswordFragment2_to_loginFragment)
        }

        resetPasswordButton.setOnClickListener {
            validateResetPassword(enterEmailET.text.toString(), confirmPasswordET.text.toString())
        }
    }

    fun validateResetPassword(email: String, confirmEmail: String): Boolean {

        if (email.isEmpty() || confirmEmail.isEmpty()) {

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

        if (email != confirmEmail) {

            ToastUtils.showToast(
                "The emails are different!",
                R.drawable.ic_baseline_remove_circle_outline_24,
                R.color.red,
                Toast.LENGTH_SHORT
            )

            return false
        }

        mainViewModel.resetPassword(email)

        ToastUtils.showToast(
            "Success, please check your email.",
            R.drawable.ic_baseline_check_circle_outline_24,
            R.color.green,
            Toast.LENGTH_LONG
        )

        return true
    }

    override fun unsubscribeUi() {

    }
}