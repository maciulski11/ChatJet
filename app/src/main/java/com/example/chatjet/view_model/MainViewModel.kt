package com.example.chatjet.view_model

import android.view.View
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatjet.R
import com.example.chatjet.data.model.Chat
import com.example.chatjet.data.model.User
import com.example.chatjet.services.FirebaseRepository

class MainViewModel: ViewModel() {

    private val repository = FirebaseRepository()

    var usersList = MutableLiveData<ArrayList<User>>()

    fun fetchUsers(){
        repository.fetchUsersList{
            usersList.postValue(it)
        }
    }

    fun fetchFullNameUser(userUid: String, v: View){
        repository.fetchFullNameUser(userUid) {
            val fullName = v.findViewById<TextView>(R.id.nameUser)
            fullName.text = it?.full_name
        }
    }
}