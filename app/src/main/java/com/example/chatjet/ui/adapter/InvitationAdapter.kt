package com.example.chatjet.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatjet.R
import com.example.chatjet.data.model.InvitationReceived

class InvitationAdapter(
    var invitationsList: ArrayList<InvitationReceived>,
    val onFriendAction: (String) -> Unit
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

        fun bind(invitation: InvitationReceived) {

            onFriendAction(invitation.uid ?: "")

        }
    }
}