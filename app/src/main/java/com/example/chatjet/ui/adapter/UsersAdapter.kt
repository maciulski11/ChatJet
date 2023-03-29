package com.example.chatjet.ui.adapter

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatjet.R
import com.example.chatjet.data.model.Friend
import com.example.chatjet.services.s.repository.FirebaseRepository
import kotlinx.android.synthetic.main.item_user.view.*
import java.util.*
import kotlin.collections.ArrayList

class UsersAdapter(var friendsList: ArrayList<Friend>, private val v: View): RecyclerView.Adapter<UsersAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_user, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val friend = friendsList[position]

        holder.chooseUser.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable(
                "friend",
                Friend(
                   friend.uid,
                    ""
                )
            )
            v.findNavController().navigate(R.id.action_usersFragment_to_chatFragment, bundle)
        }

        holder.bind(friend.uid ?: "", friend.uidLastMessage ?: "")
    }

    override fun getItemCount(): Int = friendsList.size

    inner class MyViewHolder(private var view: View) : RecyclerView.ViewHolder(view) {

        val chooseUser = view.findViewById<ConstraintLayout>(R.id.chooseUser)!!
        private val icon = view.findViewById<ImageView>(R.id.userPhoto)

        fun bind(uidFriend: String, uidMessage: String) {

            FirebaseRepository().fetchFriends(uidFriend) { f ->
                view.fullName.text = f.full_name

                Glide.with(view)
                    .load(f.photo)
                    .override(220,220)
                    .circleCrop()
                    .into(icon)
            }

            FirebaseRepository().fetchLastMessage(uidMessage) { m ->
                // SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("pl")) add polish language
                val dateFormat = SimpleDateFormat("d MMM, HH:mm", Locale("pl"))
                view.time.text = dateFormat.format(m.sentAt)
                view.lastMessage.text = m.message
            }

        }
    }
}