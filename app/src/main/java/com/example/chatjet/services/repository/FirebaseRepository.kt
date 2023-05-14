package com.example.chatjet.services.repository

import android.annotation.SuppressLint
import android.util.Log
import com.example.chatjet.data.model.Chat
import com.example.chatjet.data.model.InvitationReceived
import com.example.chatjet.data.model.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import java.util.*
import kotlin.collections.ArrayList

class FirebaseRepository {

    private val db = FirebaseFirestore.getInstance()
    private val fbAuth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()

    val currentUserUid: String
        get() = fbAuth.currentUser?.uid ?: ""

    companion object {
        const val USERS = "users"
        const val FRIENDS = "friends"
        const val INVITATIONS_SENT = "invitations_sent"
        const val INVITATIONS_RECEIVED = "invitations_received"
    }

    fun getCurrentUserName(onSuccess: (String) -> Unit) {
        db.collection(USERS).document(currentUserUid)
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
        val docRef = db.collection(USERS).document(currentUserUid)

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

        // Pobranie listy znajomych użytkownika
        db.collection(USERS)
            .document(currentUserUid)
            .get()
            .addOnSuccessListener { userResult ->
                val currentUser = userResult.toObject(User::class.java)
                val friendsUid = currentUser?.friends?.map { it.uid } ?: arrayListOf()

                db.collection(USERS)
                    .limit(12)
                    .whereNotIn("uid", friendsUid)
//                    .whereNotEqualTo("uid", currentUserUid)
                    .get()
                    .addOnSuccessListener { usersResult ->
                        db.collection(USERS)
                            .document(currentUserUid)
                            .collection(INVITATIONS_SENT)
                            .get()
                            .addOnSuccessListener { invitationResult ->
                                val users = usersResult.toObjects(User::class.java)
                                val invitations = invitationResult.toObjects(User::class.java)
                                val invitedUid = invitations.map { it.uid }

                                // Utworzenie listy użytkowników, którzy nie otrzymali zaproszeń
                                val list = arrayListOf<User>()
                                for (user in users) {
                                    if (user.uid != currentUserUid && !invitedUid.contains(user.uid)) {
                                        list.add(user)
                                    }
                                }

                                onComplete.invoke(list)
                            }
                            .addOnFailureListener { exception ->
                                Log.e("Jest blad", exception.message.toString())
                                onComplete.invoke(arrayListOf())
                            }
                    }
            }
    }

    fun updateInvitationsList(success: () -> Unit) {
        val docRef = db.collection(USERS).document(currentUserUid ?: "")
            .collection(INVITATIONS_RECEIVED)

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

    fun fetchInvitationsList(onComplete: (ArrayList<InvitationReceived>) -> Unit) {
        db.collection(USERS).document(currentUserUid)
            .collection(INVITATIONS_RECEIVED)
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

                    val list = arrayListOf<InvitationReceived>()
                    for (dc: DocumentChange in value!!.documentChanges) {
                        //sprawdxzamy czy dokument zostal poprawnie dodany:
                        if (dc.type == DocumentChange.Type.ADDED) {
                            list.add(dc.document.toObject(InvitationReceived::class.java))
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

    fun updateDataOfUser(name: String, number: String?, location: String, status: Boolean) {

        // Tworzenie mapy z danymi do zaktualizowania
        val updates = hashMapOf<String, Any>()

        // Dodawanie tylko tych pól, które zostały zmienione
        if (name.isNotEmpty()) {
            updates["full_name"] = name
        }

        if (number!!.isNotBlank()) {
            updates["number"] = number.toIntOrNull() ?: 0
        }

        if (location.isNotEmpty()) {
            updates["location"] = location
        }

        updates["status"] = status


        db.collection(USERS).document(FirebaseRepository().currentUserUid)
            .update(updates)
            .addOnSuccessListener {
                Log.d("TAG", "Użytkownik został zaktualizowany pomyślnie")
            }
            .addOnFailureListener { e ->
                Log.e("TAG", "Błąd podczas aktualizowania użytkownika", e)
            }
    }

    //wczytanie zdjecia
    fun uploadUserPhoto(bytes: ByteArray) {
        storage.getReference("users")
            .child("${currentUserUid}.jpg")
            .putBytes(bytes)
            .addOnCompleteListener {
                Log.d("REPO_DEBUG", "COMPLETE UPLOAD PHOTO")
            }
            .addOnSuccessListener {
                getPhotoDownloadUrl(it.storage)

            }
            .addOnFailureListener {
                Log.d("REPO_DEBUG", it.message.toString())
            }
    }

    private fun getPhotoDownloadUrl(storage: StorageReference) {
        storage.downloadUrl
            .addOnSuccessListener {
                updateUserPhoto(it.toString())
            }
            .addOnFailureListener {
                Log.d("REPO_DEBUG", it.message.toString())
            }
    }

    private fun updateUserPhoto(url: String?) {
        db.collection(USERS)
            .document(currentUserUid)
            .update("photo", url)
            .addOnSuccessListener {
                Log.d("REPO_DEBUG", "UPDATE USER PHOTO!")
            }
            .addOnFailureListener {
                Log.d("REPO_DEBUG", it.message.toString())
            }
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

    fun fetchUserOrFriend(uid: String, onComplete: (User?) -> Unit) {
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
        val receiverDocRef = db.collection(USERS).document(currentUserUid)

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
                set("sentAt", Timestamp.now())

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

    fun sendInvitation(uid: String) {

        val dataReceived = hashMapOf(
            "uid" to currentUserUid,
            "accept" to false,
            "status" to "new"
        )

        val db = FirebaseFirestore.getInstance()
        db.collection(USERS).document(uid)
            .collection(INVITATIONS_RECEIVED).document(currentUserUid)
            .set(dataReceived)
            .addOnSuccessListener {
                Log.d("TAG", "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error writing document", e)
            }

        val dataSent = hashMapOf(
            "uid" to uid,
            "accept" to false
        )

        db.collection(USERS).document(currentUserUid)
            .collection(INVITATIONS_SENT).document(uid)
            .set(dataSent)
            .addOnSuccessListener {
                Log.d("TAG", "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error writing document", e)
            }

        // TODO:
        //Zrobione dla latwiejszego sprawdzania apki
        val dataSentewf = hashMapOf(
            "uid" to uid,
            "accept" to false)
        db.collection(USERS).document(currentUserUid)
            .collection(INVITATIONS_RECEIVED).document(uid)
            .set(dataSentewf)
            .addOnSuccessListener {
                Log.d("TAG", "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error writing document", e)
            }

    }

    private fun transferDocUid(docUid: String) {
        Log.d("TAG", "transfer docUid: $docUid")
    }

    fun acceptInvitation(uid: String) {

        val message = "Hello, we are friends right now and we can chat."

        sendMessage(currentUserUid, uid, message) { docUid ->
            transferDocUid(docUid)

            val sender = hashMapOf(
                "uid" to uid,
                "uidLastMessage" to docUid,
                "readMessage" to true,
                "sentAt" to Timestamp.now()
            )

            db.collection(USERS).document(currentUserUid)
                .update("friends", FieldValue.arrayUnion(sender))
                .addOnSuccessListener {
                    // Dodanie do listy zakończone sukcesem
                    Log.d("TAG", "Dodano do listy!")
                }
                .addOnFailureListener {
                    // Błąd podczas dodawania do listy
                    Log.d("TAG", "Błąd podczas dodawania do listy: ${it.message}")
                }

            val receiver = hashMapOf(
                "uid" to currentUserUid,
                "uidLastMessage" to docUid,
                "readMessage" to false,
                "sentAt" to Timestamp.now()
            )

            db.collection(USERS).document(uid)
                .update("friends", FieldValue.arrayUnion(receiver))
                .addOnSuccessListener {
                    // Dodanie do listy zakończone sukcesem
                    Log.d("TAG", "Dodano do listy!")
                }
                .addOnFailureListener {
                    // Błąd podczas dodawania do listy
                    Log.d("TAG", "Błąd podczas dodawania do listy: ${it.message}")
                }
        }
    }

    fun deleteFriend(uid: String) {
        db.collection(USERS).document(currentUserUid)
            .delete()
            .addOnSuccessListener {
                Log.d("TAG", "DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error deleting document", e)
            }
    }

    fun deleteInvitation(uid: String) {
        db.collection(USERS).document(FirebaseRepository().currentUserUid)
            .collection(INVITATIONS_RECEIVED).document(uid)
            .delete()
            .addOnSuccessListener {
                Log.d("TAG", "DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error deleting document", e)
            }

        db.collection(USERS).document(uid)
            .collection(INVITATIONS_SENT).document(currentUserUid)
            .delete()
            .addOnSuccessListener {
                Log.d("TAG", "DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error deleting document", e)
            }
    }
}

