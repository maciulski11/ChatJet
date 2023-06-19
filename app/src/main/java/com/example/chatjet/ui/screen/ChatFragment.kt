package com.example.chatjet.ui.screen

import android.icu.text.SimpleDateFormat
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatjet.R
import com.example.chatjet.RetrofitInstance
import com.example.chatjet.base.BaseFragment
import com.example.chatjet.data.model.*
import com.example.chatjet.services.repository.FirebaseRepository
import com.example.chatjet.services.utils.AnimationUtils
import com.example.chatjet.ui.adapter.ChatAdapter
import com.example.chatjet.view_model.ChatViewModel
import com.example.chatjet.view_model.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class ChatFragment : BaseFragment() {
    override val layout: Int = R.layout.fragment_chat

    private lateinit var chatList: ArrayList<Chat>
    private lateinit var adapter: ChatAdapter

    private val db = FirebaseFirestore.getInstance()

    // Added object which registration listener in Firebase
    private var chatListenerRegistration: ListenerRegistration? = null

    private val viewModel: ChatViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

    override fun subscribeUi() {

        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.visibility = View.GONE

        val friend = requireArguments().getParcelable<Friend>("friend")
        val userUid = friend?.uid!!

        // Wczytanie elementów w recycler view od dołu:
        layoutManager.stackFromEnd = true

        val chatRecyclerView = view?.findViewById<RecyclerView>(R.id.chatRecyclerView)
        chatRecyclerView?.layoutManager = layoutManager
        chatRecyclerView?.setHasFixedSize(true)

        chatList = arrayListOf()

        mainViewModel.fetchUserOrFriend(userUid) {
            nameUser.text = it?.full_name
        }

//        chatRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
//                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
//                if (firstVisibleItemPosition == 0) {
//                    readMessage(currentUserUid!!, userUid, limit)
//                    limit += 25 // Zwiększ limit o kolejne 25 wiadomości przy każdym wczytywaniu
//                }
//            }
//        })

        returnBT.setOnClickListener {
            // Exit from listener when you click button return
            chatListenerRegistration?.remove()
            findNavController().navigate(
                R.id.action_chatFragment_to_usersFragment,
                null,
                AnimationUtils.topNavAnim
            )
        }

        sendMessage.setOnClickListener {
            val message = writeMessage.text.toString()

            if (message.isEmpty()) {
                return@setOnClickListener

            } else {

                sendMessage(FirebaseRepository().currentUserUid, userUid, message)
                Log.d("REPOUSER", "$userUid, $message, ${FirebaseRepository().currentUserUid}")
                writeMessage.setText("")

                FirebaseRepository().getCurrentUserName { userName ->

                    FirebaseRepository().fetchTokenUser(userUid) { token ->

                        sendNotification(token ?: "", userName, message, FirebaseRepository().currentUserUid)
                    }
                }
            }
        }

        readMessage(FirebaseRepository().currentUserUid, userUid)

    }

    private fun sendMessage(senderId: String, receiverId: String, message: String) {

        viewModel.sendMessage(senderId, receiverId, message)

    }

    private fun readMessage(senderId: String, receiverId: String) {

        // Implemented object listener which is listening change in Firebase
        db.collection(FirebaseRepository.CHAT).document(senderId)
            .collection(receiverId)
            .orderBy("sentAt", Query.Direction.DESCENDING)
//            .limit(20)
            .addSnapshotListener { snapshot, error ->
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
                        chatList.sortByDescending { it.sentAt }

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

                    if (chatRecyclerView == null) {
                        Log.d("TAG", "chatRecyclerView is null")
                        return@addSnapshotListener
                    }

                    //TODO: rozwiazac problem z wczytywaniem wiadomosci na gorze i pozniej na dole
                    // Reverse the order of chatGroupList with a delay
                    chatRecyclerView.postDelayed({

                        chatRecyclerView.post {
                            adapter = ChatAdapter(chatList)
                            chatRecyclerView.adapter = adapter
                            Log.d("REPOADAPTER", "$adapter")
                            chatRecyclerView.scrollToPosition(adapter.itemCount - 1)
                        }

                    }, 250)

                } else {
                    // List is empty, load empty layout
                    chatList.clear()
                }
            }
    }

    private fun getDateString(date: Date): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }

    private fun sendNotification(token: String, userName: String, messageContent: String, userUid: String) {

        CoroutineScope(Dispatchers.IO).launch {

            val not = PushNotification(
                NotificationData(userName, messageContent, userUid),
                token
            )

            try {
                val response = RetrofitInstance.api.postNotification(not)

                if (response.isSuccessful) {
                    Log.d("TAG", "Response: $response")
                    Log.d("REPO_NOTIFICATION", "fun sendNotification")
                } else {
                    Log.e("TAG", response.errorBody()!!.string())
                    Log.d("REPO_NOTIFICATION", "fun sendNotification error")
                }
            } catch (e: Exception) {
                Log.e("TAG", e.toString())
                Log.d("REPO_NOTIFICATION", "fun sendNotification exception")
            }
        }
    }

    override fun unsubscribeUi() {

    }
}
