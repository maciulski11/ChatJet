package com.example.chatjet.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatjet.data.model.Friend
import com.example.chatjet.data.model.User
import com.example.chatjet.services.s.repository.FirebaseRepository

class MainViewModel: ViewModel() {

    private val repository = FirebaseRepository()

    var users: MutableLiveData<User?> = MutableLiveData()
    var usersList = MutableLiveData<ArrayList<User>>()

    val friends : ArrayList<Friend>
        get() = users.value?.friends ?: arrayListOf()


    fun fetchUsers() {
        repository.fetchUsersList {
            usersList.postValue(it)
        }
    }

    fun sendMessage(senderId: String, receiverId: String, message: String) {
        repository.sendMessage(senderId, receiverId, message) { docUid ->
        }
    }

    fun fetchUserOrFriend(userUid: String, onComplete: (User?) -> Unit) {
        repository.fetchUserOrFriend(userUid) {
            onComplete.invoke(it)

        }
    }
}