package com.example.chatjet.services.repository

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.util.Log
import com.example.chatjet.data.model.*
import com.example.chatjet.ui.adapter.ChatAdapter
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_chat.*
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
        const val CHAT = "chat"
        const val USERS = "users"
        const val FRIENDS = "friends"
        const val TOKEN = "token"
        const val INVITATIONS_SENT = "invitations_sent"
        const val INVITATIONS_RECEIVED = "invitations_received"
    }

    fun registerUser(
        email: String,
        fullName: String,
        number: Int,
        password: String,
    ) {
        fbAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResults ->
                if (authResults.user != null) {
                    val user = User(
                        authResults.user!!.email,
                        authResults.user!!.uid,
                        "",
                        fullName,
                        "",
                        number,
                        true
                    )
                    db.collection(USERS)
                        .document(currentUserUid)
                        .set(user)

                    //send verification email to your account
                    fbAuth.currentUser?.sendEmailVerification()
                        ?.addOnCompleteListener {
                            Log.d("Email verify", "Everything is ok, email sent!")
                        }
                }
            }
            .addOnFailureListener { exception ->

                Log.e("Something went wrong", exception.message.toString())
            }
    }

    fun loginUser(
        email: String,
        password: String,
        onLoginSuccess: (FirebaseUser) -> Unit,
        onVerifyEmail: () -> Unit,
        onNotExistUser: () -> Unit,
    ) {
        //we check that this data is in our datebase
        fbAuth.signInWithEmailAndPassword(
            email,
            password
        ).addOnSuccessListener { authRes ->

            if (authRes != null) {

                // Check verified your email
                if (fbAuth.currentUser!!.isEmailVerified) {

                    FirebaseMessaging.getInstance().token
                        .addOnCompleteListener { task ->
                            if (!task.isSuccessful) {
                                Log.w(
                                    "LoginFragment",
                                    "Fetching FCM registration token failed",
                                    task.exception
                                )
                                return@addOnCompleteListener
                            }

                            // Get new FCM registration token
                            val token = task.result

                            db.collection(USERS).document(currentUserUid)
                                .update(TOKEN, token)

                            // Log the token
                            Log.d("LoginFragment", "FCM registration token: $token")
                        }

                    onLoginSuccess(fbAuth.currentUser!!)

                } else {

                    onVerifyEmail()
                }
            }
        }
            .addOnFailureListener { exception ->

                onNotExistUser()
                Log.d("DEBUG", exception.message.toString())
            }
    }

    fun firstLogin() {
        db.collection(USERS).document(currentUserUid)
            .update("firstLogin", false)
    }

    // Fun of reset password
    fun resetPassword(email: String) {
        val auth = FirebaseAuth.getInstance()

        // Send email with link of change password
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("Sent email with link of reset password")
                    // Wyświetl informację dla użytkownika, że email został wysłany
                } else {
                    // Obsłuż błąd wysyłki emaila
                    println("Eerror sent of email: ${task.exception?.message}")
                }
            }
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

    fun fetchUsersList(onComplete: (ArrayList<User>) -> Unit) {
        db.collection(USERS)
            .document(currentUserUid)
            .get()
            .addOnSuccessListener { userResult ->
                val currentUser = userResult.toObject(User::class.java)
                val friendsUid = currentUser?.friends?.map { it.uid } ?: arrayListOf()

                db.collection(USERS)
                    .limit(20)
                    .get()
                    .addOnSuccessListener { usersResult ->
                        val users = usersResult.toObjects(User::class.java)

                        db.collection(USERS)
                            .document(currentUserUid)
                            .collection(INVITATIONS_SENT)
                            .get()
                            .addOnSuccessListener { invitationResult ->
                                val invitations = invitationResult.toObjects(User::class.java)
                                val invitedUid = invitations.map { it.uid }

                                // Utworzenie listy użytkowników, którzy nie otrzymali zaproszeń
                                val list = arrayListOf<User>()
                                for (user in users) {
                                    if (user.uid != currentUserUid && !invitedUid.contains(user.uid) && !friendsUid.contains(
                                            user.uid
                                        )
                                    ) {
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
        val docRef = db.collection(USERS).document(currentUserUid)
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

    fun fetchFriends(uid: String, onComplete: (User?) -> Unit): DocumentReference {
        val docRef = db.collection(USERS).document(uid)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val user = snapshot.toObject(User::class.java)
                Log.d("TAG", "Current data: $user")
                onComplete.invoke(user)
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

    // Loading photo
    fun uploadUserPhoto(bytes: ByteArray) {
        storage.getReference(USERS)
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

    //TODO: do przeniesienia kodu z fragmenty chat
    fun fetchChat(senderId: String, receiverId: String, onComplete: (ArrayList<Chat>) -> Unit) {
        db.collection(CHAT).document(senderId)
            .collection(receiverId)
            .orderBy("sentAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("TAG", "Listen failed.", error)
                    return@addSnapshotListener
                }

                val chatList = arrayListOf<Chat>()

                if (snapshot != null && !snapshot.isEmpty) {
                    for (document in snapshot.documents) {
                        val chat = document.toObject(Chat::class.java)

                        if (chat?.senderId == senderId && chat.receiverId == receiverId ||
                            chat?.senderId == receiverId && chat.receiverId == senderId
                        ) {
                            chatList.add(chat)
                        }
                    }
                }

                onComplete.invoke(chatList)
            }
    }

    fun fetchLastMessage(
        senderId: String,
        receiverId: String,
        messageId: String,
        onComplete: (Chat) -> Unit
    ) {
        db.collection(CHAT).document(senderId)
            .collection(receiverId).document(messageId)
            .get().addOnSuccessListener { snapshot ->
                val chat = snapshot.toObject(Chat::class.java)
                chat?.let {
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

    fun userReadMessage(uidFriend: String) {
        // pobierz dokument użytkownika, który odbiera wiadomość
        val receiverDocRef = db.collection(USERS).document(currentUserUid)

        receiverDocRef.get().addOnSuccessListener { receiverDocSnapshot ->
            val receiverFriends =
                receiverDocSnapshot.get(FRIENDS) as ArrayList<HashMap<String, Any>>?

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

        // Save for sender
        db.collection(CHAT).document(senderId)
            .collection(receiverId)
            .add(chat)
            .addOnSuccessListener { documentReference ->
                val docUid = documentReference.id

                onSendMessageSuccess(docUid)

                updateLastMessage(senderId, receiverId, docUid)

                Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference.id}")

                // Save for receiver
                db.collection(CHAT).document(receiverId)
                    .collection(senderId)
                    .document(docUid)  // Używamy tego samego docUid
                    .set(chat)
                    .addOnSuccessListener {
                        onSendMessageSuccess(docUid)
                        updateLastMessage(receiverId, senderId, docUid)
                        Log.d("TAG", "DocumentSnapshot added with ID: $docUid")
                    }
                    .addOnFailureListener { e ->
                        Log.w("TAG", "Error adding document", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error adding document", e)
            }
    }

    fun fetchAndUpdateMessages() {
        //TODO: przeniesc funkcje z chatFragment
    }

    private fun updateLastMessage(senderId: String, receiverId: String, docUid: String) {
        // pobierz dokument użytkownika, który odbiera wiadomość
        val senderDocRef = db.collection(USERS).document(senderId)

        senderDocRef.get().addOnSuccessListener { senderDocSnapshot ->
            val senderFriends =
                senderDocSnapshot.get("friends") as ArrayList<HashMap<String, Any>>?

            // znajdź przyjaciela w liście przyjaciół użytkownika, który wysyła wiadomość i zaktualizuj jego "lastMessage"
            val friendToUpdate = senderFriends?.find { it["uid"] == receiverId }

            friendToUpdate?.apply {

                set("uidLastMessage", docUid)
                set("sentAt", Timestamp.now())
                set("readMessage", false)

            }

            friendToUpdate?.let {
                senderDocRef.update("friends", senderFriends)
            }
        }

        // pobierz dokument użytkownika, który wysyła wiadomość
        val receiverDocRef = db.collection(USERS).document(receiverId)

        receiverDocRef.get().addOnSuccessListener { receiverDocSnapshot ->
            val receiverFriends =
                receiverDocSnapshot.get("friends") as ArrayList<HashMap<String, Any>>?

            val updatedFriends = receiverFriends?.map { friend ->
                if (friend["uid"] == senderId) {
                    friend.apply {

                        set("uidLastMessage", docUid)
                        set("sentAt", Timestamp.now())
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
            "accept" to false
        )
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

            val currentUserRef = db.collection(USERS).document(currentUserUid)

            currentUserRef.get().addOnSuccessListener { snapshot ->
                val friends = snapshot.get(FRIENDS) as? ArrayList<HashMap<String, Any>>?
                val isAlreadyFriend = friends?.any { it["uid"] == uid } ?: false

                if (!isAlreadyFriend) {
                    currentUserRef
                        .update(FRIENDS, FieldValue.arrayUnion(sender))
                        .addOnSuccessListener {
                            // Dodanie do listy zakończone sukcesem
                            Log.d("TAG", "Dodano do listy!")
                        }
                        .addOnFailureListener {
                            // Błąd podczas dodawania do listy
                            Log.d("TAG", "Błąd podczas dodawania do listy: ${it.message}")
                        }
                } else {
                    // Użytkownik już znajduje się na liście
                    Log.d("TAG", "Użytkownik już znajduje się na liście!")
                }
            }.addOnFailureListener {
                // Błąd podczas pobierania danych użytkownika
                Log.d("TAG", "Błąd podczas pobierania danych użytkownika: ${it.message}")
            }

            val receiver = hashMapOf(
                "uid" to currentUserUid,
                "uidLastMessage" to docUid,
                "readMessage" to false,
                "sentAt" to Timestamp.now()
            )

            val userRef = db.collection(USERS).document(uid)

            userRef.get().addOnSuccessListener { snapshot ->
                val friends = snapshot.get(FRIENDS) as? ArrayList<HashMap<String, Any>>?
                val isAlreadyFriend = friends?.any { it["uid"] == currentUserUid } ?: false

                if (!isAlreadyFriend) {
                    userRef
                        .update(FRIENDS, FieldValue.arrayUnion(receiver))
                        .addOnSuccessListener {
                            // Dodanie do listy zakończone sukcesem
                            Log.d("TAG", "Dodano do listy!")
                        }
                        .addOnFailureListener {
                            // Błąd podczas dodawania do listy
                            Log.d("TAG", "Błąd podczas dodawania do listy: ${it.message}")
                        }
                } else {
                    // Użytkownik już znajduje się na liście
                    Log.d("TAG", "Użytkownik już znajduje się na liście!")
                }
            }.addOnFailureListener {
                // Błąd podczas pobierania danych użytkownika
                Log.d("TAG", "Błąd podczas pobierania danych użytkownika: ${it.message}")
            }
        }
    }

    fun deleteChat(messageUid: String) {
        val collectionRef = db.collection(CHAT)
            .document(currentUserUid)
            .collection(messageUid)

        collectionRef.get().addOnSuccessListener { snapshot ->
            val batch = db.batch()
            for (document in snapshot.documents) {
                batch.delete(document.reference)
            }

            // pobierz dokument użytkownika, który odbiera wiadomość
            val messageDocRef = db.collection(USERS).document(currentUserUid)

            messageDocRef.get().addOnSuccessListener { senderDocSnapshot ->
                val senderFriends =
                    senderDocSnapshot.get(FRIENDS) as ArrayList<HashMap<String, Any>>?

                // znajdź przyjaciela w liście przyjaciół użytkownika, który wysyła wiadomość i zaktualizuj jego "lastMessage"
                val friendToUpdate = senderFriends?.find { it["uid"] == messageUid }

                friendToUpdate?.apply {

                    set("uidLastMessage", "")
                }

                friendToUpdate?.let {
                    messageDocRef.update(FRIENDS, senderFriends)
                }
            }

            batch.commit().addOnCompleteListener {
                Log.d("TAG", "Collection successfully deleted!")

            }.addOnFailureListener { e ->
                Log.w("TAG", "Error deleting collection", e)
            }
        }
    }

    fun deleteFriend(friend: Friend) {
        db.collection(FirebaseRepository.USERS)
            .document(FirebaseRepository().currentUserUid)
            .update(FirebaseRepository.FRIENDS, FieldValue.arrayRemove(friend))
            .addOnSuccessListener {
                Log.d("TAG", "Friend successfully deleted!")
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error deleting friend", e)
            }
    }

    fun notAcceptInvitation(uid: String) {
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
            .collection(INVITATIONS_RECEIVED).document(currentUserUid)
            .delete()
            .addOnSuccessListener {
                Log.d("TAG", "DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error deleting document", e)
            }

        //TODO: do usuniecia, dla testów!!!
        db.collection(USERS).document(FirebaseRepository().currentUserUid)
            .collection(INVITATIONS_SENT).document(uid)
            .delete()
            .addOnSuccessListener {
                Log.d("TAG", "DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error deleting document", e)
            }
    }
}

