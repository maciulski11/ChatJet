package com.example.chatjet.ui.screen

import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatjet.R
import com.example.chatjet.base.BaseFragment
import com.example.chatjet.data.model.Chat
import com.example.chatjet.data.model.ChatGroup
import com.example.chatjet.data.model.User
import com.example.chatjet.services.FirebaseRepository
import com.example.chatjet.ui.adapter.ChatAdapter
import com.example.chatjet.view_model.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import kotlinx.android.synthetic.main.fragment_chat.*
import java.util.*
import kotlin.collections.ArrayList

class ChatFragment : BaseFragment() {
    override val layout: Int = R.layout.fragment_chat

    private var chatList = ArrayList<Chat>()
    private lateinit var adapter: ChatAdapter

    private val db = FirebaseFirestore.getInstance()
    private val fbAuth = FirebaseAuth.getInstance()

    private val currentUserUid: String?
        get() = fbAuth.currentUser?.uid

    private val dbUser = db.collection("users").document(currentUserUid!!)
    private val dbChat = db.collection("chat")

    // Added object which registration listener in Firebase
    private var chatListenerRegistration: ListenerRegistration? = null

    private val viewModel: MainViewModel by activityViewModels()

    override fun subscribeUi() {

        val userUid = requireArguments().getString("uid").toString()

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        // Wczytanie elementów w recycler view od dołu:
        layoutManager.stackFromEnd = true
        chatRecyclerView.layoutManager = layoutManager
        chatRecyclerView.setHasFixedSize(true)

        viewModel.fetchFullNameUser(userUid, requireView())

        returnBT.setOnClickListener {

            // Exit from listener when you click button return
            chatListenerRegistration!!.remove()
            findNavController().navigate(R.id.action_chatFragment_to_usersFragment)
        }

        sendMessage.setOnClickListener {
            val message = writeMessage.text.toString()

            if (message.isEmpty()) {
                return@setOnClickListener

            } else {
                sendMessage(FirebaseRepository().currentUserUid!!, userUid, message)
                Log.d("REPOUSER", "$userUid, $message")
                writeMessage.setText("")
//                dbUser.addSnapshotListener(object : EventListener<DocumentSnapshot> {
//                    override fun onEvent(
//                        snapshot: DocumentSnapshot?,
//                        error: FirebaseFirestoreException?
//                    ) {
//                        if (error != null) {
//                            Log.w("TAG", "Listen failed.", error)
//                            return
//                        }
//                        if (snapshot != null && snapshot.exists()) {
//                            val user = snapshot.toObject(User::class.java)
//                            val namee: String = user!!.full_name.toString()
//
//                            sendMessage(FirebaseRepository().currentUserUid!!, userUid, message)
//                            Log.d("REPOUSER", "$userUid, $message")
//                            writeMessage.setText("")
//                            topic = "/topics/${userUid}"
//                            PushNotification(
//                                NotificationData("${namee} :", message),
//                                topic
//                            ).also {
//                                sendNotification(it)
//                            }
//
//                            db.collection("user").document(userUid)
//                                .collection("last_message")
//                                .document(firebaseUser.uid)
//                                .update("message", message)

//                        } else {
//                            Log.d("TAG", "Current data: null")
//                        }
//                    }
//                })
            }
        }
        readMessage(currentUserUid!!, userUid)


    }

    private fun sendMessage(senderId: String, receiverId: String, message: String) {

        FirebaseRepository().sendMessage(senderId, receiverId, message)

    }

    private fun readMessage(senderId: String, receiverId: String) {

        // Implemented object listener which is listening change in Firebase
        chatListenerRegistration = dbChat.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w("TAG", "Listen failed.", error)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                chatList.clear()

                for (document in snapshot.documents) {
                    val chat = document.toObject(Chat::class.java)

                    if (chat!!.senderId == senderId && chat.receiverId == receiverId ||
                        chat.senderId == receiverId && chat.receiverId == senderId
                    ) {
                        chatList.add(chat)
                    }
                }

                // Sort the chatList by timestamp in descending order
                chatList.sortByDescending { it.timestamp }

                // If database has more than 1 message
                if (chatList.size > 1) {
                    // Reverse the chatList to display the latest messages at the bottom
                    chatList = chatList.reversed() as ArrayList<Chat>
                }

                // Group the chatList by date
                val groupedChatList =
                    chatList.groupBy { it.sentAt?.let { it1 -> getDateString(it1) } }

                // Create a new list to display chat groups
                val chatGroupList = mutableListOf<ChatGroup>()

                // Iterate through the groupedChatList and create ChatGroup objects
                for ((date, chats) in groupedChatList) {
                    chatGroupList.add(ChatGroup(date.toString(), chats))
                }

                adapter = ChatAdapter(chatList)
                chatRecyclerView.adapter = adapter
                Log.d("REPOADAPTER", "$adapter")


            } else {

                // List is empty, load empty layout
                chatList.clear()
                adapter = ChatAdapter(chatList)
                chatRecyclerView.adapter = adapter

            }
        }
    }

    private fun getDateString(date: Date): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }

    override fun unsubscribeUi() {

    }


}