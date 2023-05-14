package com.example.chatjet.ui.screen

import android.annotation.SuppressLint
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatjet.R
import com.example.chatjet.base.BaseFragment
import com.example.chatjet.data.model.InvitationReceived
import com.example.chatjet.services.repository.FirebaseRepository
import com.example.chatjet.services.utils.Utilities
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

                Utilities.customToast(
                    requireContext(),
                    "Invitation accepted!",
                    R.drawable.ic_baseline_check_circle_outline_24,
                    R.color.white,
                    R.color.green,
                    Toast.LENGTH_SHORT
                )
            },
            {
                mainViewModel.deleteInvitation(it)

                Utilities.customToast(
                    requireContext(),
                    "Invitation not accepted!!",
                    R.drawable.ic_baseline_remove_circle_outline_24,
                    R.color.white,
                    R.color.red,
                    Toast.LENGTH_SHORT
                )
            })
        recyclerViewInvitations.adapter = adapter

        mainViewModel.invitationsList.observe(this) {
            adapter.invitationsList = it
            adapter.notifyDataSetChanged()

            if (it.isEmpty()) {
                recyclerViewInvitations.visibility = View.GONE
                emptyTextView.visibility = View.VISIBLE
            } else {
                recyclerViewInvitations.visibility = View.VISIBLE
                emptyTextView.visibility = View.GONE
            }
        }

        FirebaseRepository().updateInvitationsList {
            mainViewModel.fetchInvitations()
        }

    }

    override fun unsubscribeUi() {

    }
}