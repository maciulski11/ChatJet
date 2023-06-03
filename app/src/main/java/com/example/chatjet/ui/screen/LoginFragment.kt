package com.example.chatjet.ui.screen

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.chatjet.R
import com.example.chatjet.base.BaseFragment
import com.example.chatjet.services.utils.ToastUtils
import com.example.chatjet.view_model.MainViewModel
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.emailET
import kotlinx.android.synthetic.main.fragment_login.passwordET
import kotlinx.android.synthetic.main.fragment_register.*
import kotlin.random.Random

class LoginFragment : BaseFragment() {
    override val layout: Int = R.layout.fragment_login

    private val REQUEST_PHONE_CALL = 1
    private val REQUEST_RECEIVE_NOTIFICATIONS = 2

    private val mainViewModel: MainViewModel by activityViewModels()

    @SuppressLint("SetTextI18n")
    override fun subscribeUi() {

        //TODO:
        //Create token list from one phone for different account

        stefan.setOnClickListener {
            emailET.setText("maxiokrzym@gmail.com")
            passwordET.setText("00000000")
            validateOnLogin(emailET.text.toString(), passwordET.text.toString())
        }

        loginBT.setOnClickListener {
            validateOnLogin(emailET.text.toString(), passwordET.text.toString())
        }

        registerBT.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
        }

        forgotPasswordButton.setOnClickListener {
            loginLayout.visibility = View.GONE
            resetPasswordLayout.visibility = View.VISIBLE
        }

        resetPasswordButton.setOnClickListener {
            validateResetPassword(enterEmailET.text.toString(), confirmEmailET.text.toString())
        }

        returnButton.setOnClickListener {
            resetPasswordLayout.visibility = View.GONE
            loginLayout.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()

        checkNotificationAndCallPermission()
    }

    private fun checkNotificationAndCallPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Jeśli nie ma uprawnień, wyświetl prośbę o uprawnienia
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CALL_PHONE),
                REQUEST_PHONE_CALL
            )
        }
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Jeśli nie ma uprawnień, wyświetl prośbę o uprawnienia
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_RECEIVE_NOTIFICATIONS
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
    fun validateOnLogin(email: String, password: String): Boolean {

        if (email.isEmpty() || password.isEmpty()) {
            ToastUtils.showToast(
                "All fields must be completed!",
                R.drawable.ic_baseline_remove_circle_outline_24,
                R.color.red,
                Toast.LENGTH_SHORT
            )
            return false
        }

        mainViewModel.loginUser(email, password, findNavController())

        return true
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

        resetPasswordLayout.visibility = View.GONE
        loginLayout.visibility = View.VISIBLE
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