package com.example.chatjet.services.s.repository

import android.util.Log
import android.view.View
import com.example.chatjet.data.model.Friend
import com.example.chatjet.data.model.User
import com.example.chatjet.ui.adapter.UsersAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import java.time.ZoneOffset
import java.util.Date
import java.util.HashMap
import kotlin.collections.ArrayList

class FirebaseRepository {

    private val db = FirebaseFirestore.getInstance()
    private val fbAuth = FirebaseAuth.getInstance()

    val currentUserUid: String?
        get() = fbAuth.currentUser?.uid

    private val dbUser = db.collection("users").document(currentUserUid!!)
    private val dbChat = db.collection("chat")

    companion object {
        const val USERS = "users"
        const val FRIENDS = "friends"
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

    fun fetchFriends(onComplete: (User) -> Unit) {
        db.collection(USERS).document(currentUserUid!!)
            .get().addOnSuccessListener { snapshot ->
                snapshot.toObject(User::class.java)?.let {
                    Log.d("REPO FetchAdditions", it.toString())
                    onComplete.invoke(it)

                }
            }
            .addOnFailureListener {
                Log.d("REPO", it.toString())
            }
    }

    fun fetchFullNameUser(uid: String, onComplete: (User?) -> Unit) {
        // Load full name user to you write
        db.collection(USERS).document(uid)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(User::class.java)
                    onComplete(user)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting user document: ", exception)
            }
    }

    fun sendMessage(senderId: String, receiverId: String, message: String, friendIndex: Int) {
        val currentTime = FieldValue.serverTimestamp()
        val chat = hashMapOf(
            "senderId" to senderId,
            "receiverId" to receiverId,
            "message" to message,
            "sentAt" to currentTime
        )

        db.collection("chat")
            .add(chat)
            .addOnSuccessListener { documentReference ->
                Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error adding document", e)
            }

//        // pobierz dokument użytkownika, który wysyła wiadomość
//        val senderDocRef = db.collection("users").document(receiverId)
//
//        senderDocRef.get().addOnSuccessListener { senderDocSnapshot ->
//            val senderFriends = senderDocSnapshot.get("friends") as ArrayList<HashMap<String, Any>>?
//
//            // znajdź przyjaciela w liście przyjaciół użytkownika, który wysyła wiadomość i zaktualizuj jego "lastMessage"
//            senderFriends?.let{ friend ->
//                val friends = friend.find { it["uid"] == receiverId }
//
//                friends?.set("lastMessage", message)
//                friends?.set("sentAt", Date())
//                senderDocRef.update("friends", senderFriends)
//            }
//
//        }

        // pobierz dokument użytkownika, który wysyła wiadomość
        val senderDocRef = db.collection("users").document(senderId)

        senderDocRef.get().addOnSuccessListener { senderDocSnapshot ->
            val senderFriends = senderDocSnapshot.get("friends") as ArrayList<HashMap<String, Any>>?

            // znajdź przyjaciela w liście przyjaciół użytkownika, który wysyła wiadomość i zaktualizuj jego "lastMessage"
            val updatedFriends = senderFriends?.map { friend ->
                if (friend["uid"] == receiverId) {
                    friend.apply {

                        //TODO: Repair time
                        set("lastMessage", message)
                        set("sentAt", Date())
                    }
                } else {
                    friend
                }
            }

            updatedFriends?.let {
                senderDocRef.update("friends", it)
            }
        }
    }
}