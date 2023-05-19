package com.example.chatjet.view_model

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.chatjet.R
import com.example.chatjet.data.model.InvitationReceived
import com.example.chatjet.data.model.User
import com.example.chatjet.services.repository.FirebaseRepository
import com.example.chatjet.services.utils.Utilities

class MainViewModel(var user: User? = null) : ViewModel() {


    private val repository = FirebaseRepository()

    var users: MutableLiveData<User?> = MutableLiveData(null)
    var usersList = MutableLiveData<ArrayList<User>>()
    var invitationsList = MutableLiveData<ArrayList<InvitationReceived>>()

    fun registerUser(email: String, fullName: String, number: Int, password: String, onNavigate: () -> Unit) {
        repository.registerUser(email, fullName, number, password)
            {
                onNavigate()
            }
    }

    fun fetchUsers() {
        repository.fetchUsersList {
            usersList.postValue(it)
        }
    }

    fun updateDataOfUser(
        name: String,
        number: String?,
        location: String,
        status: Boolean,
        navController: NavController
    ) {
        repository.updateDataOfUser(name, number, location, status)
        navController.navigate(R.id.action_profileEditFragment_to_profileFragment)
    }

    fun uploadUserPhoto(bytes: ByteArray) {
        FirebaseRepository().uploadUserPhoto(bytes)
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

    fun sendInvitation(uid: String) {
        repository.sendInvitation(uid)
    }

    fun acceptInvitation(uid: String) {
        repository.acceptInvitation(uid)
    }

    fun deleteInvitation(uid: String) {
        repository.deleteInvitation(uid)
    }

    fun fetchInvitations() {
        repository.fetchInvitationsList {
            invitationsList.postValue(it)
        }
    }
}