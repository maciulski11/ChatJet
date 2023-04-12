package com.example.chatjet.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatjet.R
import com.example.chatjet.data.model.Chat
import com.example.chatjet.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatAdapter(private val chatList: ArrayList<Chat>): RecyclerView.Adapter<ChatAdapter.MyViewHolder>() {

    private val fbAuth = FirebaseAuth.getInstance()

    private val currentUserUid: String?
        get() = fbAuth.currentUser?.uid

    companion object {
        private const val MESSAGE_TYPE_LEFT = 0
        private const val MESSAGE_TYPE_RIGHT = 1
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
        return if (chatList[position].senderId == FirebaseAuth.getInstance().uid) {
            MESSAGE_TYPE_RIGHT
        } else {
            MESSAGE_TYPE_LEFT
        }

    }

    inner class MyViewHolder(private var view: View): RecyclerView.ViewHolder(view) {

        private val message = view.findViewById<TextView>(R.id.messageTV)
        private val image = view.findViewById<ImageView>(R.id.photo)


        fun bindView(chat: Chat) {
            message.text = chat.message


                // Load current user's image from Firestore and display it in ImageView
//                FirebaseFirestore.getInstance().collection("users").document("RxT9gnqCC2ZHcNYLtRVqgbpND113").get()
//                    .addOnSuccessListener { document ->
//                        if (document != null && document.exists()) {
//                            val user = document.toObject(User::class.java)
//                            val imageUrl = user?.photo
//                            Glide.with(view)
//                                .load(imageUrl)
//                                .override(74, 74)
//                                .circleCrop()
//                                .into(image)
//                        }
//                    }
//                // Load receiver's image from Firestore and display it in ImageView
//                FirebaseFirestore.getInstance().collection("users").document("aCvaRpWTtFchtSzCDjB9aN7GdBB3").get()
//                    .addOnSuccessListener { document ->
//                        if (document != null && document.exists()) {
//                            val user = document.toObject(User::class.java)
//                            val imageUrl = user?.photo
//                            Glide.with(view)
//                                .load(imageUrl)
//                                .override(74, 74)
//                                .circleCrop()
//                                .into(image)
//
//                    }
//            }

//            // Load receiver's image from Firestore and display it in ImageView
//            FirebaseFirestore.getInstance().collection("users").document(chat.receiverId!!).get()
//                .addOnSuccessListener { document ->
//                    if (document != null && document.exists()) {
//                        val user = document.toObject(User::class.java)
//                        val imageUrl = user?.photo
//                            Glide
//                                .with(context)
//                                .load(imageUrl)
//                                .override(120, 120)
//                                .circleCrop()
//                                .into(image)
//
//                    }
//                }
        }
    }
}