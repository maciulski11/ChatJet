package com.example.chatjet.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.example.chatjet.R
import com.example.chatjet.base.BaseFragment
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

    private val REQUEST_PHONE_CALL = 1

    companion object {
        private const val TAGG = "MyFirebaseMessagingService"
    }

    @SuppressLint("SetTextI18n")
    override fun subscribeUi() {

        // sprawdzenie, czy aplikacja ma uprawnienie do dzwonienia
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // jeśli nie ma uprawnień, sprawdź, czy użytkownik już udzielił uprawnień
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CALL_PHONE)) {
                // użytkownik jeszcze nie udzielił uprawnień, wyświetl prośbę o uprawnienia
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CALL_PHONE), REQUEST_PHONE_CALL)
            }
        }


        // sprawdzenie, czy aplikacja ma uprawnienie do dzwonienia
//        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//            // jeśli nie ma uprawnień, poproś użytkownika o ich udzielenie
//            // użytkownik nie udzielił uprawnień, wyświetl komunikat informujący o wymaganych uprawnieniach
//            AlertDialog.Builder(requireContext())
//                .setTitle("Uprawnienia do dzwonienia")
//                .setMessage("Aby korzystać z aplikacji, wymagane są uprawnienia do dzwonienia.")
//                .setPositiveButton("Ustawienia") { _, _ ->
//                    // przejdź do ustawień aplikacji, gdzie użytkownik może ręcznie udzielić uprawnień
//                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + requireContext().packageName))
//                    intent.addCategory(Intent.CATEGORY_DEFAULT)
//                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                    startActivity(intent)
//                    activity?.finish()
//                }
//                .setNegativeButton("Anuluj") { _, _ ->
//                    // użytkownik anulował wybór, zamknij aplikację
//                    activity?.finish()
//                }
//                .setCancelable(false)
//                .show()
////            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CALL_PHONE), REQUEST_PHONE_CALL)
//        }

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


//    @Deprecated("Deprecated in Java")
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        when (requestCode) {
//            REQUEST_PHONE_CALL -> {
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // użytkownik udzielił uprawnień, zaloguj się do aplikacji
//                    // ...
//                } else {
//                    // użytkownik nie udzielił uprawnień, wyświetl komunikat i zamknij aplikację
//                    Toast.makeText(requireContext(), "Aby korzystać z aplikacji, musisz udzielić uprawnień do dzwonienia.", Toast.LENGTH_LONG).show()
//                   requireActivity().finish()
//                }
//                return
//            }
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//    }


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

                        if (authRes != null){

                            FirebaseMessaging.getInstance().token
                                .addOnCompleteListener { task ->
                                    if (!task.isSuccessful) {
                                        Log.w(TAGG, "Fetching FCM registration token failed", task.exception)
                                        return@addOnCompleteListener
                                    }

                                    // Get new FCM registration token
                                    val token = task.result

                                    db.collection("users").document(fbUser?.uid ?: "").update("token", token)

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