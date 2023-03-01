package com.example.chatjet.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
}