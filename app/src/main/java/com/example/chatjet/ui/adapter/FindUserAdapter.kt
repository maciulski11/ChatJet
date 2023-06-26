package com.example.chatjet.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.chatjet.R
import com.example.chatjet.models.data.User
import kotlinx.android.synthetic.main.item_find_user.view.*

class FindUserAdapter(
    var usersList: ArrayList<User>,
    val context: Context,
    val onFetchUser: (String, View) -> Unit,
    val onSend: (String) -> Unit
) :
    RecyclerView.Adapter<FindUserAdapter.MyViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun setFilteredList(usersList: ArrayList<User>) {
        this.usersList = usersList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_find_user, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = usersList[position]

        holder.bind(user)
    }

    override fun getItemCount(): Int = usersList.size

    inner class MyViewHolder(private var view: View) : RecyclerView.ViewHolder(view) {

        private val inviteButton = view.findViewById<ImageButton>(R.id.inviteButton)

        fun bind(user: User) {

            val uid = user.uid ?: ""

            onFetchUser(uid, view)

            inviteButton.setOnClickListener {

                onSend(uid)
            }
        }
    }
}