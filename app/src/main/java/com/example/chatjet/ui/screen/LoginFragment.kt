package com.example.chatjet.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.chatjet.R
import com.example.chatjet.base.BaseFragment
import com.example.chatjet.services.utils.Utilities
import com.example.chatjet.view_model.MainViewModel
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.emailET
import kotlinx.android.synthetic.main.fragment_login.passwordET
import kotlinx.android.synthetic.main.fragment_register.*

class LoginFragment : BaseFragment() {
    override val layout: Int = R.layout.fragment_login

    private val REQUEST_PHONE_CALL = 1
    private val REQUEST_RECEIVE_NOTIFICATIONS = 2

    private val mainViewModel: MainViewModel by activityViewModels()

    @SuppressLint("SetTextI18n")
    override fun subscribeUi() {

        //TODO:
        //Create token list from one phone for different account

        macio.setOnClickListener {
            emailET.setText("macio@wp.pl")
            passwordET.setText("00000000")
            validateOnLogin(emailET.text.toString(), passwordET.text.toString())
        }

        stefan.setOnClickListener {
            emailET.setText("maxiokrzym@gmail.com")
            passwordET.setText("Q1111111")
            validateOnLogin(emailET.text.toString(), passwordET.text.toString())
        }

        loginBT.setOnClickListener {
            validateOnLogin(emailET.text.toString(), passwordET.text.toString())
        }

        registerBT.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
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
    fun validateOnLogin(
        email: String,
        password: String
    ): Boolean {

        if (email.isEmpty() || password.isEmpty()) {
            Utilities.showToast(
                "All fields must be completed!",
                R.color.red,
                R.drawable.ic_baseline_remove_circle_outline_24,
                Toast.LENGTH_SHORT
            )
            return false
        }

        mainViewModel.loginUser(email, password, findNavController(), requireContext())

        return true
    }

    override fun unsubscribeUi() {

    }
}