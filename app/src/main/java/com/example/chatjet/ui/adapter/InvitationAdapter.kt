package com.example.chatjet.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatjet.R
import com.example.chatjet.data.model.InvitationReceived
import com.example.chatjet.services.repository.FirebaseRepository

class InvitationAdapter(
    var invitationsList: ArrayList<InvitationReceived>,
    val context: Context,
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

    inner class MyViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private val photo = view.findViewById<ImageView>(R.id.photo)
        private val nameUser = view.findViewById<TextView>(R.id.nameUser)
        private val location = view.findViewById<TextView>(R.id.locationTV)
        private val acceptButton = view.findViewById<ImageButton>(R.id.acceptButton)
        private val unacceptedButton = view.findViewById<ImageButton>(R.id.unacceptedButton)

        fun bind(invitation: InvitationReceived) {

            val uid = invitation.uid ?: ""

            FirebaseRepository().fetchFriends(uid) { user ->
                nameUser.text = user.full_name
                location.text = user.location

                Glide.with(view)
                    .load(user.photo)
                    .circleCrop()
                    .into(photo)

                acceptButton.setOnClickListener {

                    onAccept(uid)
                    onDelete(uid)
                }

                unacceptedButton.setOnClickListener {

                    onDelete(uid)
                }

            }
        }
    }
}