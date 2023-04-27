package com.example.chatjet.ui.screen

import android.annotation.SuppressLint
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatjet.R
import com.example.chatjet.base.BaseFragment
import com.example.chatjet.data.model.InvitationReceived
import com.example.chatjet.services.s.repository.FirebaseRepository
import com.example.chatjet.ui.adapter.InvitationAdapter
import com.example.chatjet.view_model.MainViewModel
import kotlinx.android.synthetic.main.fragment_find_user.*
import kotlinx.android.synthetic.main.fragment_invitation.*

class InvitationFragment : BaseFragment() {
    override val layout: Int = R.layout.fragment_invitation

    private var invitationsList = ArrayList<InvitationReceived>()
    private lateinit var adapter: InvitationAdapter
    private val mainViewModel: MainViewModel by activityViewModels()

    @SuppressLint("NotifyDataSetChanged")
    override fun subscribeUi() {

        recyclerViewInvitations.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewInvitations.setHasFixedSize(true)

        invitationsList = arrayListOf()

        adapter = InvitationAdapter(invitationsList, requireContext(),
            {
                mainViewModel.acceptInvitation(it)
            },
            {
                mainViewModel.deleteInvitation(it)
            })
        recyclerViewInvitations.adapter = adapter

        mainViewModel.invitationsList.observe(this) {
            adapter.invitationsList = it
            adapter.notifyDataSetChanged()
        }

        FirebaseRepository().updateInvitationsList {
            mainViewModel.fetchInvitations()
        }

    }

    override fun unsubscribeUi() {

    }
}