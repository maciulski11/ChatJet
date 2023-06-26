package com.example.chatjet.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.chatjet.R
import com.example.chatjet.models.data.InvitationReceived

class InvitationAdapter(
    var invitationsList: ArrayList<InvitationReceived>,
    val onFetchFriend: (String, View) -> Unit,
    val onAccept: (String) -> Unit,
    val onDelete: (String) -> Unit
) :
    RecyclerView.Adapter<InvitationAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_ivitation, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val invitation = invitationsList[position]

        holder.bind(invitation)
    }

    override fun getItemCount(): Int = invitationsList.size

    inner class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        private val acceptButton = view.findViewById<ImageButton>(R.id.acceptButton)
        private val unacceptedButton = view.findViewById<ImageButton>(R.id.unacceptedButton)

        fun bind(invitation: InvitationReceived) {

            val friendUid = invitation.uid ?: ""

            // Call the onFetchFriend callback function with the friendUid and the itemView
            onFetchFriend(friendUid, view)

            acceptButton.setOnClickListener {
                
                onAccept(friendUid)
            }

            unacceptedButton.setOnClickListener {
                
                onDelete(friendUid)
            }
        }
    }
}