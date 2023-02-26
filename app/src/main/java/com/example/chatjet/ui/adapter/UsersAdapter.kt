package com.example.chatjet.ui.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.chatjet.R
import com.example.chatjet.data.model.User
import kotlinx.android.synthetic.main.item_user.view.*

class UsersAdapter(var usersList: ArrayList<User>, private val v: View): RecyclerView.Adapter<UsersAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_user, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user: User = usersList[position]

        holder.chooseUser.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(
                "uid",
                user.uid
            )
            v.findNavController().navigate(R.id.action_usersFragment_to_chatFragment, bundle)
        }

        holder.bind(user)
    }

    override fun getItemCount(): Int = usersList.size

    inner class MyViewHolder(private var v: View) : RecyclerView.ViewHolder(v) {

        val chooseUser = v.findViewById<ConstraintLayout>(R.id.chooseUser)!!

        fun bind(u: User) {
            v.name.text = u.full_name

        }
    }
}