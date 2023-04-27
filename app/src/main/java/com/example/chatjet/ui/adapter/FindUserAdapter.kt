package com.example.chatjet.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatjet.R
import com.example.chatjet.data.model.User
import kotlinx.android.synthetic.main.item_find_user.view.*

class FindUserAdapter(
    var usersList: ArrayList<User>,
    val context: Context,
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

        private val photo = view.findViewById<ImageView>(R.id.photo)
        private val nameUser = view.findViewById<TextView>(R.id.nameUser)
        private val location = view.findViewById<TextView>(R.id.locationTV)
        private val inviteButton = view.findViewById<ImageButton>(R.id.inviteButton)


        fun bind(user: User) {
            nameUser.text = user.full_name
            location.text = user.location

            Glide.with(view)
                .load(user.photo)
                .circleCrop()
                .into(photo)

            inviteButton.setOnClickListener {

                Toast.makeText(context, "Invited!", Toast.LENGTH_SHORT).show()

                onSend(user.uid ?: "")
            }
        }
    }
}