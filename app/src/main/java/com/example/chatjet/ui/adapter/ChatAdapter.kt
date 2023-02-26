package com.example.chatjet.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatjet.R
import com.example.chatjet.data.model.Chat
import com.google.firebase.auth.FirebaseAuth

class ChatAdapter(private val chatList: ArrayList<Chat>): RecyclerView.Adapter<ChatAdapter.MyViewHolder>() {

    private val fbAuth = FirebaseAuth.getInstance()

    private val currentUserUid: String?
        get() = fbAuth.currentUser?.uid

    companion object {
        private val MESSAGE_TYPE_LEFT = 0
        private val MESSAGE_TYPE_RIGHT = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return if (viewType == MESSAGE_TYPE_RIGHT) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_sent_message, parent, false)
            MyViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_receive_message, parent, false)
            MyViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val chat = chatList[position]

        holder.bindView(chat)

    }

    override fun getItemCount(): Int = chatList.size

    override fun getItemViewType(position: Int): Int {
        return if (chatList[position].senderId == currentUserUid) {
            MESSAGE_TYPE_RIGHT
        } else {
            MESSAGE_TYPE_LEFT
        }

    }

    inner class MyViewHolder(private val v: View): RecyclerView.ViewHolder(v) {

        val message = v.findViewById<TextView>(R.id.messageTV)

        fun bindView(chat: Chat) {
            message.text = chat.message
        }
    }
}