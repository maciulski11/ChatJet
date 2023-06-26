package com.example.chatjet.models.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatjet.models.data.User
import com.example.chatjet.services.repository.FirebaseRepository

class FindUserViewModel: ViewModel() {

    val repository = FirebaseRepository()

    var usersList = MutableLiveData<ArrayList<User>>()

    fun fetchUsersList() {
        repository.fetchUsersList {
            usersList.postValue(it)
        }
    }

    fun sendInvitation(uid: String) {
        repository.sendInvitation(uid)
        fetchUsersList()
    }
}