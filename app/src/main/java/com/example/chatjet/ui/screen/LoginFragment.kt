package com.example.chatjet.ui.screen

import android.util.Log
import androidx.navigation.fragment.findNavController
import com.example.chatjet.R
import com.example.chatjet.base.BaseFragment
import com.example.chatjet.data.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment: BaseFragment() {
    override val layout: Int = R.layout.fragment_login

    private val fbAuth = FirebaseAuth.getInstance()
    private val fbUser = fbAuth.currentUser
    private val db = FirebaseFirestore.getInstance()

    companion object {
        private const val TAGG = "MyFirebaseMessagingService"
    }

    override fun subscribeUi() {

        loginClick()

    }

    private fun loginClick() {
        loginBT.setOnClickListener {
            val email = emailET.text.toString()
            val password = passwordET.text.toString()

            if (email == "" || password == "") {
                return@setOnClickListener
            } else {

                //we check that this data is in our datebase
                fbAuth.signInWithEmailAndPassword(
                    email,
                    password
                )
                    .addOnSuccessListener { authRes ->

                        if (authRes != null){

                            FirebaseMessaging.getInstance().token
                                .addOnCompleteListener { task ->
                                    if (!task.isSuccessful) {
                                        Log.w(TAGG, "Fetching FCM registration token failed", task.exception)
                                        return@addOnCompleteListener
                                    }

                                    // Get new FCM registration token
                                    val token = task.result

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
    }

    override fun unsubscribeUi() {

    }
}