package com.example.chatjet.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.example.chatjet.R
import com.example.chatjet.base.BaseFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : BaseFragment() {
    override val layout: Int = R.layout.fragment_login

    private val fbAuth = FirebaseAuth.getInstance()
    private val fbUser = fbAuth.currentUser
    private val db = FirebaseFirestore.getInstance()

    private val REQUEST_PHONE_CALL = 1
    private val REQUEST_RECEIVE_NOTIFICATIONS = 2

    companion object {
        private const val TAGG = "MyFirebaseMessagingService"
    }

    @SuppressLint("SetTextI18n")
    override fun subscribeUi() {

        macio.setOnClickListener {
            emailET.setText("macio@wp.pl")
            passwordET.setText("00000000")

            loginClick()
        }

        stefan.setOnClickListener {
            emailET.setText("stefan@wp.pl")
            passwordET.setText("00000000")

            loginClick()
        }

        loginBT.setOnClickListener {
            loginClick()
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


    private fun loginClick() {
        val email = emailET.text.toString()
        val password = passwordET.text.toString()

        if (email == "" || password == "") {
            return
        } else {

            //we check that this data is in our datebase
            fbAuth.signInWithEmailAndPassword(
                email,
                password
            )
                .addOnSuccessListener { authRes ->

                    if (authRes != null) {

                        FirebaseMessaging.getInstance().token
                            .addOnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    Log.w(
                                        TAGG,
                                        "Fetching FCM registration token failed",
                                        task.exception
                                    )
                                    return@addOnCompleteListener
                                }

                                // Get new FCM registration token
                                val token = task.result

                                db.collection("users").document(fbUser?.uid ?: "")
                                    .update("token", token)

                                // Log the token
                                Log.d(TAGG, "FCM registration token: $token")
                            }


                        findNavController().navigate(R.id.action_loginFragment_to_usersFragment)
                    }
                }
                .addOnFailureListener { exception ->
                    Snackbar.make(
                        requireView(),
                        "Your account is not exist.",
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                    Log.d("DEBUG", exception.message.toString())
                }
        }


    }

    override fun unsubscribeUi() {

    }
}