package com.example.chatjet.services.s.repository

import android.annotation.SuppressLint
import android.util.Log
import com.example.chatjet.data.model.Chat
import com.example.chatjet.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import java.util.HashMap
import kotlin.collections.ArrayList

class FirebaseRepository {

    private val db = FirebaseFirestore.getInstance()
    private val fbAuth = FirebaseAuth.getInstance()

    val currentUserUid: String?
        get() = fbAuth.currentUser?.uid

    companion object {
        const val USERS = "users"
        const val FRIENDS = "friends"
    }

    fun getCurrentUserName(onSuccess: (String) -> Unit) {
        db.collection(USERS).document(currentUserUid!!)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val userName = documentSnapshot.getString("full_name")
                userName?.let { onSuccess(it) }
            }
    }

    fun fetchTokenUser(userUid: String, onTokenFetched: (token: String?) -> Unit) {
        db.collection(USERS).document(userUid).get().addOnSuccessListener { documentSnapshot ->
            val token = documentSnapshot.getString("token")
            onTokenFetched(token)
        }
    }

    fun updateUsersList(success: () -> Unit) {
        val docRef = db.collection(USERS).document(currentUserUid!!)

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
            .addSnapshotListener(object : EventListener<QuerySnapshot> {

                @SuppressLint("NotifyDataSetChanged")
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ) {

                    if (error != null) {
                        Log.e("Jest blad", error.message.toString())
                        onComplete.invoke(arrayListOf())
                        return
                    }

                    val list = arrayListOf<User>()
                    for (dc: DocumentChange in value!!.documentChanges) {
                        //sprawdxzamy czy dokument zostal poprawnie dodany:
                        if (dc.type == DocumentChange.Type.ADDED) {

                            list.add(dc.document.toObject(User::class.java))
                        }
                    }

                    onComplete.invoke(list)
                }
            })
    }

    fun fetchFriends(uid: String, onComplete: (User) -> Unit): DocumentReference {
        val docRef = db.collection(USERS).document(uid)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val user = snapshot.toObject(User::class.java)
                Log.d("TAG", "Current data: $user")
                onComplete.invoke(user!!)
            } else {
                Log.d("TAG", "Current data: null")
            }
        }
        return docRef
    }

    fun fetchLastMessage(uid: String, onComplete: (Chat) -> Unit) {
        db.collection("chat").document(uid)
            .get().addOnSuccessListener { snapshot ->
                snapshot.toObject(Chat::class.java)?.let {
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

    fun readMessage(uidFriend: String) {
        // pobierz dokument użytkownika, który odbiera wiadomość
        val receiverDocRef = db.collection(USERS).document(currentUserUid!!)

        receiverDocRef.get().addOnSuccessListener { receiverDocSnapshot ->
            val receiverFriends =
                receiverDocSnapshot.get("friends") as ArrayList<HashMap<String, Any>>?

            // znajdź przyjaciela w liście przyjaciół użytkownika, który odbiera wiadomość i zaktualizuj jego "lastMessage"
            val updatedFriends = receiverFriends?.map { friend ->
                if (friend["uid"] == uidFriend) {
                    friend.apply {

                        set("readMessage", true)

                    }
                } else {
                    friend
                }
            }

            updatedFriends?.let {
                receiverDocRef.update("friends", it)
            }

        }
    }

    fun sendMessage(
        senderId: String,
        receiverId: String,
        message: String,
        onSendMessageSuccess: (docUid: String) -> Unit
    ) {
        val currentTime = FieldValue.serverTimestamp()
        val chat = hashMapOf(
            "senderId" to senderId,
            "receiverId" to receiverId,
            "message" to message,
            "sentAt" to currentTime,

            )

        db.collection("chat")
            .add(chat)
            .addOnSuccessListener { documentReference ->
                val docUid = documentReference.id
                onSendMessageSuccess(docUid)

                updateLastMessage(senderId, receiverId, docUid)

                Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error adding document", e)
            }

    }

    private fun updateLastMessage(senderId: String, receiverId: String, docUid: String) {
        // pobierz dokument użytkownika, który wysyła wiadomość
        val senderDocRef = db.collection("users").document(senderId)

        senderDocRef.get().addOnSuccessListener { senderDocSnapshot ->
            val senderFriends =
                senderDocSnapshot.get("friends") as ArrayList<HashMap<String, Any>>?

            // znajdź przyjaciela w liście przyjaciół użytkownika, który wysyła wiadomość i zaktualizuj jego "lastMessage"
            val friendToUpdate = senderFriends?.find { it["uid"] == receiverId }
            friendToUpdate?.apply {
                set("uidLastMessage", docUid)

            }

            friendToUpdate?.let {
                senderDocRef.update("friends", senderFriends)
            }
        }

        // pobierz dokument użytkownika, który odbiera wiadomość
        val receiverDocRef = db.collection("users").document(receiverId)

        receiverDocRef.get().addOnSuccessListener { receiverDocSnapshot ->
            val receiverFriends =
                receiverDocSnapshot.get("friends") as ArrayList<HashMap<String, Any>>?

            // znajdź przyjaciela w liście przyjaciół użytkownika, który odbiera wiadomość i zaktualizuj jego "lastMessage"
            val updatedFriends = receiverFriends?.map { friend ->
                if (friend["uid"] == senderId) {
                    friend.apply {

                        set("uidLastMessage", docUid)
                        set("readMessage", false)

                    }
                } else {
                    friend
                }
            }

            updatedFriends?.let {
                receiverDocRef.update("friends", it)
            }

        }
    }
}

