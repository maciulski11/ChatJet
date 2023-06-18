package com.example.chatjet.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatjet.data.model.Chat
import com.example.chatjet.services.repository.FirebaseRepository

class ChatViewModel: ViewModel() {

    val repository = FirebaseRepository()

    var chatList = MutableLiveData<ArrayList<Chat>>()

    fun sendMessage(senderId: String, receiverId: String, message: String) {
        repository.sendMessage(senderId, receiverId, message) {
        }
    }

    fun fetchChat(senderId: String, receiverId: String, onComplete: () -> Unit) {
        repository.fetchChat(senderId, receiverId) {
            chatList.postValue(it)
            onComplete()
        }
    }
}