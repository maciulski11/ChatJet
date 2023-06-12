package com.example.chatjet.view_model

import android.app.AlertDialog
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.chatjet.R
import com.example.chatjet.data.model.InvitationReceived
import com.example.chatjet.data.model.User
import com.example.chatjet.services.repository.FirebaseRepository
import com.example.chatjet.services.utils.ToastUtils
import kotlin.collections.ArrayList

class MainViewModel(var user: User? = null) : ViewModel() {

    private val repository = FirebaseRepository()

    var users: MutableLiveData<User?> = MutableLiveData(null)
    var usersList = MutableLiveData<ArrayList<User>>()
    var invitationsList = MutableLiveData<ArrayList<InvitationReceived>>()

    fun loginUser(email: String, password: String, navController: NavController) {
        repository.loginUser(email, password,
            {
                navController.navigate(R.id.action_loginFragment_to_messageFragment)
            },
            {
                ToastUtils.showToast(
                    "Please verify your email!",
                    R.drawable.ic_baseline_remove_circle_outline_24,
                    R.color.red,
                    Toast.LENGTH_SHORT
                )
            },
            {
                ToastUtils.showToast(
                    "Email or password is incorrect!",
                    R.drawable.ic_baseline_remove_circle_outline_24,
                    R.color.red,
                    Toast.LENGTH_SHORT
                )
            })
    }

    fun registerUser(email: String, fullName: String, number: Int, password: String) {
        repository.registerUser(email, fullName, number, password)
    }

    fun resetPassword(email: String) {
        repository.resetPassword(email)
    }

    fun firstLogin() {
        repository.firstLogin()
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

    fun deleteConversation(messageUid: String, view: View, fullName: String) {
        val alertDialog = AlertDialog.Builder(view.context)
            .setTitle("Delete chat!")
            .setMessage("Do you want to delete your messages with ${fullName}?")
            .setPositiveButton("OK") { dialog, which ->
                // Obsługa kliknięcia przycisku OK

                repository.deleteConversation(messageUid)

            }
            .setNegativeButton("Anuluj") { dialog, which ->
                // Obsługa kliknięcia przycisku Anuluj
            }
            .create()

        alertDialog.show()
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