package com.example.chatjet.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.chatjet.R
import com.example.chatjet.base.BaseFragment
import com.example.chatjet.services.utils.AnimationUtils
import com.example.chatjet.services.utils.ToastUtils
import com.example.chatjet.models.view_model.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.emailET
import kotlinx.android.synthetic.main.fragment_login.passwordET
import kotlinx.android.synthetic.main.fragment_register.*

class LoginFragment : BaseFragment() {
    override val layout: Int = R.layout.fragment_login

    private val REQUEST_PHONE_CALL = 1
    private val REQUEST_RECEIVE_NOTIFICATIONS = 2

    private val mainViewModel: MainViewModel by activityViewModels()
    private var currentUser: FirebaseUser? = null

    @SuppressLint("SetTextI18n")
    override fun subscribeUi() {

        //TODO: Create token list from one phone for different account

        stefan.setOnClickListener {
            emailET.setText("maxiokrzym@gmail.com")
            passwordET.setText("00000000")
            validateOnLogin(emailET.text.toString(), passwordET.text.toString())
        }

        loginBT.setOnClickListener {
            validateOnLogin(emailET.text.toString(), passwordET.text.toString())
        }

        registerBT.setOnClickListener {
            findNavController().navigate(
                R.id.action_loginFragment_to_registrationFragment,
                null,
                AnimationUtils.leftNavAnim
            )
        }

        forgotPasswordButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_loginFragment_to_resetPasswordFragment2,
                null,
                AnimationUtils.rightNavAnim
            )
        }
    }

    override fun onResume() {
        super.onResume()

        checkNotificationAndCallPermission()

        val sharedPrefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val myArgument = sharedPrefs.getString("myArgument", null)

        if (myArgument == null) {
            // Jeśli wartość argumentu jest null, wykonaj normalne logowanie
            currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                // Użytkownik jest już zalogowany, przekieruj go do odpowiedniego ekranu
                findNavController().navigate(R.id.action_loginFragment_to_messageFragment)
            }
        }
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

        mainViewModel.loginUser(email, password) { user ->
            currentUser = user

            val sharedPrefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            sharedPrefs.edit().putString("myArgument", null).apply()

            findNavController().navigate(R.id.action_loginFragment_to_messageFragment)
        }

        return true
    }

    override fun unsubscribeUi() {

    }
}