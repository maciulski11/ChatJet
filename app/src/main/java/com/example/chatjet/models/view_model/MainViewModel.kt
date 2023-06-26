package com.example.chatjet.models.view_model

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.chatjet.R
import com.example.chatjet.models.data.Friend
import com.example.chatjet.models.data.User
import com.example.chatjet.services.repository.FirebaseRepository
import com.example.chatjet.services.utils.ToastUtils
import com.google.firebase.auth.FirebaseUser

class MainViewModel(var user: User? = null) : ViewModel() {

    private val repository = FirebaseRepository()

    var users: MutableLiveData<User?> = MutableLiveData(null)

    fun loginUser(email: String, password: String, onLoginSuccess: (FirebaseUser) -> Unit) {
        repository.loginUser(email, password,
            { user ->
                onLoginSuccess(user)
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

    fun userReadMessage(uidFriend: String) {
        repository.userReadMessage(uidFriend)
    }

    fun deleteChat(messageUid: String) {
        repository.deleteChat(messageUid)
    }

    fun fetchUserOrFriend(userUid: String, onComplete: (User?) -> Unit) {
        repository.fetchUserOrFriend(userUid) {
            onComplete.invoke(it)
        }
    }

    fun deleteFriend(friend: Friend) {
        repository.deleteFriend(friend)
    }

    fun fetchFriend(uidFriend: String, onComplete: (User?) -> Unit) {
        repository.fetchFriends(uidFriend) { friend ->
            onComplete(friend)
        }
    }
}