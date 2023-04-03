package com.example.chatjet.view_model

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatjet.R
import com.example.chatjet.data.model.Friend
import com.example.chatjet.data.model.User
import com.example.chatjet.services.s.repository.FirebaseRepository

class MainViewModel: ViewModel() {

    private val repository = FirebaseRepository()

    var users: MutableLiveData<User?> = MutableLiveData()
    var usersList = MutableLiveData<ArrayList<User>>()

    val friends : ArrayList<Friend>
        get() = users.value?.friends ?: arrayListOf()

    fun updateSom(success: () -> Unit) {
        repository.updateUsersList {
            success
        }
    }

    fun fetchUsers() {
        repository.fetchUsersList {
            usersList.postValue(it)
        }
    }

    fun sendMessage(senderId: String, receiverId: String, message: String, success: () -> Unit) {
        repository.sendMessage(senderId, receiverId, message)
    }

    fun fetchFullNameUser(userUid: String, v: View, context: Context) {
        repository.fetchFullNameUser(userUid) {
            val fullName = v.findViewById<TextView>(R.id.nameUser)
//            val photo = v.findViewById<ImageView>(R.id.photo)
            fullName.text = it?.full_name


//            Glide.with(context)
//                .load(it?.photo)
//                .override(120, 120)
//                .circleCrop()
//                .into(photo)
        }
    }
}