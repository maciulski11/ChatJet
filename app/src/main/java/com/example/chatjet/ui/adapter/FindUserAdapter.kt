package com.example.chatjet.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatjet.R
import com.example.chatjet.data.model.Friend
import com.example.chatjet.data.model.User
import kotlinx.android.synthetic.main.item_find_user.view.*

class FindUserAdapter(var usersList: ArrayList<User>): RecyclerView.Adapter<FindUserAdapter.MyViewHolder>() {

    private var friends: ArrayList<Friend> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    fun update(friends: ArrayList<Friend>) {

        this.friends.addAll(friends)

        notifyDataSetChanged()
    }

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

        private val photo = view.findViewById<ImageView>(R.id.photo)
        private val inviteButton = view.findViewById<ImageButton>(R.id.inviteButton)
        private val removeButton = view.findViewById<ImageButton>(R.id.removeButton)

        fun bind(user: User) {
            view.nameUser.text = user.full_name

            Glide.with(view)
                .load(user.photo)
                .circleCrop()
                .into(photo)

            inviteButton.setOnClickListener{
                inviteButton.visibility = View.GONE
                removeButton.visibility = View.VISIBLE
            }

            removeButton.setOnClickListener {
                inviteButton.visibility = View.VISIBLE
                removeButton.visibility = View.GONE
            }

        }
    }
}