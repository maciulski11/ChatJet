package com.example.chatjet.services

import android.annotation.SuppressLint
import android.util.Log
import com.example.chatjet.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class FirebaseRepository {

    private val fbAuth = FirebaseAuth.getInstance()
    private val fbUser = fbAuth.currentUser
    private val db = FirebaseFirestore.getInstance()

    private val currentUserUid: String?
        get() = fbAuth.currentUser?.uid

    companion object {
        const val USERS = "users"
    }

    fun updateUsersList(success: () -> Unit) {
        val docRef = db.collection(USERS)

        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                // Update your UI with the new data here
                success()
            } else {
                Log.d("TAG", "Current data: null")
            }
        }
    }

    fun fetchUsersList(onComplete: (ArrayList<User>) -> Unit) {
        db.collection(USERS)
            // Wykluczenie z wczytania użytkownika, którego uid jest równe zalogowanemu użytkownikwi
            .whereNotEqualTo("uid", currentUserUid)
            .limit(9)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list = arrayListOf<User>()
                    for (document in task.result!!) {
                        val user = document.toObject(User::class.java)
                        list.add(user)
                    }
                    onComplete.invoke(list)
                } else {
                    Log.e("Jest blad", task.exception?.message.toString())
                    onComplete.invoke(arrayListOf())
                }
            }
    }
}