package com.example.chatjet.models.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatjet.models.data.InvitationReceived
import com.example.chatjet.services.repository.FirebaseRepository

class InvitationViewModel : ViewModel() {

    val repository = FirebaseRepository()

    var invitationsList = MutableLiveData<ArrayList<InvitationReceived>>()

     private fun fetchInvitations() {
        repository.fetchInvitationsList {
            invitationsList.postValue(it)
        }
    }

    fun fetchAndUpdateInvitationsList() {
        repository.updateInvitationsList {
            fetchInvitations()
        }
    }

    fun acceptInvitation(uid: String) {
        repository.acceptInvitation(uid)
    }

    fun notAcceptInvitation(uid: String) {
        repository.notAcceptInvitation(uid)
    }
}