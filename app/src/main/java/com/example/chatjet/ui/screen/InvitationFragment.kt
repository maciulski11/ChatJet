package com.example.chatjet.ui.screen

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatjet.R
import com.example.chatjet.base.BaseFragment
import com.example.chatjet.data.model.InvitationReceived
import com.example.chatjet.services.utils.ToastUtils
import com.example.chatjet.ui.adapter.InvitationAdapter
import com.example.chatjet.view_model.InvitationViewModel
import com.example.chatjet.view_model.MainViewModel
import kotlinx.android.synthetic.main.fragment_find_user.*
import kotlinx.android.synthetic.main.fragment_invitation.*
import kotlinx.android.synthetic.main.item_ivitation.*

//TODO: sprawdzicz czemu powiadomienie znika tylko po wejsciu w messageFragment

class InvitationFragment : BaseFragment() {
    override val layout: Int = R.layout.fragment_invitation

    private lateinit var invitationsList: ArrayList<InvitationReceived>
    private lateinit var adapter: InvitationAdapter
    private val viewModel: InvitationViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    @SuppressLint("NotifyDataSetChanged")
    override fun subscribeUi() {

        recyclerViewInvitations.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewInvitations.setHasFixedSize(true)

        invitationsList = arrayListOf()

        adapter = InvitationAdapter(
            invitationsList,
            { friendUid, itemView ->

                fetchFriend(friendUid, itemView)
            },
            { friendUid ->

                acceptInvitation(friendUid)
            },
            { friendUid ->

                notAcceptInvitation(friendUid)
            }
        )

        recyclerViewInvitations.adapter = adapter

        viewModel.invitationsList.observe(this) {
            adapter.invitationsList = it
            adapter.notifyDataSetChanged()

            if (adapter.invitationsList.isEmpty()) {
                recyclerViewInvitations.visibility = View.GONE
                emptyTextView.visibility = View.VISIBLE
            } else {
                recyclerViewInvitations.visibility = View.VISIBLE
                emptyTextView.visibility = View.GONE
            }
        }

        viewModel.fetchAndUpdateInvitationsList()

    }

    private fun fetchFriend(friendUid: String, itemView: View) {

        val nameUser = itemView.findViewById<TextView>(R.id.nameUser)
        val locationTV = itemView.findViewById<TextView>(R.id.locationTV)
        val photo = itemView.findViewById<ImageView>(R.id.photo)

        mainViewModel.fetchFriend(friendUid) { friend ->

            nameUser.text = friend?.full_name
            locationTV.text = friend?.location

            Glide.with(requireView())
                .load(friend?.photo)
                .circleCrop()
                .into(photo)
        }
    }

    private fun acceptInvitation(friendUid: String) {

        viewModel.acceptInvitation(friendUid)
        viewModel.notAcceptInvitation(friendUid)

        ToastUtils.showToast(
            "Invitation accepted!",
            R.drawable.ic_baseline_check_circle_outline_24,
            R.color.green,
            Toast.LENGTH_SHORT
        )
    }

    private fun notAcceptInvitation(friendUid: String) {

        viewModel.notAcceptInvitation(friendUid)

        ToastUtils.showToast(
            "Invitation not accepted!",
            R.drawable.ic_baseline_remove_circle_outline_24,
            R.color.red,
            Toast.LENGTH_SHORT
        )
    }

    override fun unsubscribeUi() {
        // Unsubscribes from observing the invitations list LiveData in the ViewModel
        viewModel.invitationsList.removeObservers(viewLifecycleOwner)
    }
}