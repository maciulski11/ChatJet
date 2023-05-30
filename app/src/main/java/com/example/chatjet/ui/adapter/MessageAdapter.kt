package com.example.chatjet.ui.adapter

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatjet.R
import com.example.chatjet.data.model.Friend
import com.example.chatjet.services.repository.FirebaseRepository
import java.util.*
import kotlin.collections.ArrayList

class MessageAdapter(var messageList: ArrayList<Friend>, private val v: View) :
    RecyclerView.Adapter<MessageAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_user, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val message = messageList[position]

        holder.chooseUser.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable(
                "friend",
                Friend(
                    message.uid,
                    ""
                )
            )

            if (message.readMessage == false) {
                holder.readMessage(message.uid!!)
            }

            v.findNavController().navigate(R.id.action_usersFragment_to_chatFragment, bundle)
        }

        // Set listener to long click
        holder.chooseUser.setOnLongClickListener(holder)
        holder.bind(message, message.uid ?: "", message.uidLastMessage ?: "")
    }

    override fun getItemCount(): Int = messageList.size

    inner class MyViewHolder(private var view: View) : RecyclerView.ViewHolder(view),
        View.OnLongClickListener {

        val chooseUser = view.findViewById<ConstraintLayout>(R.id.chooseUser)!!
        private val icon = view.findViewById<ImageView>(R.id.userPhoto)
        private val message = view.findViewById<TextView>(R.id.lastMessage)
        private val time = view.findViewById<TextView>(R.id.time)
        private val name = view.findViewById<TextView>(R.id.fullName)
        private val status = view.findViewById<ImageView>(R.id.statusColor)

        fun readMessage(uidFriend: String) {
            FirebaseRepository().readMessage(uidFriend)
        }

        fun bind(friend: Friend, uidFriend: String, uidMessage: String) {

            FirebaseRepository().fetchFriends(uidFriend) { f ->

                if (friend.readMessage == false) {
                    name.setTextColor(Color.BLACK)
                    name.setTypeface(null, Typeface.BOLD)
                    message.setTextColor(Color.BLACK)
                    message.setTypeface(null, Typeface.BOLD)// pogrubienie czcionki
                    time.setTextColor(Color.BLACK)
                    time.setTypeface(null, Typeface.BOLD)

                } else {
                    name.setTextColor(Color.GRAY)
                    name.setTypeface(null, Typeface.BOLD)
                    message.setTextColor(Color.GRAY)
                    message.setTypeface(null, Typeface.NORMAL)
                    time.setTextColor(Color.GRAY)
                    time.setTypeface(null, Typeface.NORMAL)
                }

                name.text = f.full_name

                if (f.photo?.isEmpty() == true || f.photo == "") {

                    Glide.with(view)
                        .load(R.drawable.ic_baseline_account_circle_240)
                        .override(220, 220)
                        .circleCrop()
                        .into(icon)

                } else {

                    Glide.with(view)
                        .load(f.photo)
                        .override(220, 220)
                        .circleCrop()
                        .into(icon)
                }
            }

            FirebaseRepository().fetchLastMessage(uidMessage) { m ->
                // SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("pl")) add polish language
                val dateFormat = SimpleDateFormat("d MMM, HH:mm", Locale("pl"))
                time.text = dateFormat.format(m.sentAt)
                message.text = m.message

            }

            FirebaseRepository().fetchUserOrFriend(uidFriend) { user ->
                if (user?.status == true) {
                    status.setColorFilter(Color.GREEN)
                } else {
                    status.setColorFilter(Color.RED)
                }
            }
        }

        override fun onLongClick(view: View): Boolean {
            val message = messageList[adapterPosition]

            FirebaseRepository().fetchFriends(message.uid.toString()) { f ->

                val alertDialog = AlertDialog.Builder(view.context)
                    .setTitle("Delete chat!")
                    .setMessage("Do you want to delete your messages with ${f.full_name}?")
                    .setPositiveButton("OK") { dialog, which ->
                        // Obsługa kliknięcia przycisku OK
                    }
                    .setNegativeButton("Anuluj") { dialog, which ->
                        // Obsługa kliknięcia przycisku Anuluj
                    }
                    .create()

                alertDialog.show()
            }
            return true
        }
    }
}