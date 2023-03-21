package com.example.chatjet.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.chatjet.R
import com.example.chatjet.data.model.Friend
import kotlinx.android.synthetic.main.item_user.view.*

class UsersAdapter(var friendsList: ArrayList<Friend>, private val v: View): RecyclerView.Adapter<UsersAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_user, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val friend: Friend = friendsList[position]

//        val icon = holder.itemView.findViewById<ImageView>(R.id.userPhoto)
//        Glide.with(holder.itemView)
//            .load(user.photo)
//            .override(220,220)
//            .circleCrop()
//            .into(icon)

//        holder.chooseUser.setOnClickListener {
//            val bundle = Bundle()
//            bundle.putParcelable(
//                "user",
//                User(
//                    "",
//                    user.uid,
//                    user.token,
//                    user.full_name,
//                    "",
//                )
//            )
//            v.findNavController().navigate(R.id.action_usersFragment_to_chatFragment, bundle)
//        }

        v.fullName.text = friend.uid
        Log.d("REPOOO", "${friend.uid}")
//        holder.bind(friend)
    }

    override fun getItemCount(): Int = friendsList.size

    inner class MyViewHolder(private var view: View) : RecyclerView.ViewHolder(view) {

        val chooseUser = view.findViewById<ConstraintLayout>(R.id.chooseUser)!!

        fun bind(friend: Friend) {
            view.fullName.text = friend.uid

        }
    }
}

//class UsersAdapter(var usersList: ArrayList<User>, private val v: View): RecyclerView.Adapter<UsersAdapter.MyViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//        val itemView =
//            LayoutInflater.from(parent.context)
//                .inflate(R.layout.item_user, parent, false)
//
//        return MyViewHolder(itemView)
//    }
//
//    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        val user: User = usersList[position]
//
//        val icon = holder.itemView.findViewById<ImageView>(R.id.userPhoto)
//        Glide.with(holder.itemView)
//            .load(user.photo)
//            .override(220,220)
//            .circleCrop()
//            .into(icon)
//
//        holder.chooseUser.setOnClickListener {
//            val bundle = Bundle()
//            bundle.putParcelable(
//                "user",
//                User(
//                    "",
//                    user.uid,
//                    user.token,
//                    user.full_name,
//                    "",
//                )
//            )
//            v.findNavController().navigate(R.id.action_usersFragment_to_chatFragment, bundle)
//        }
//
//        holder.bind(user)
//    }
//
//    override fun getItemCount(): Int = usersList.size
//
//    inner class MyViewHolder(private var v: View) : RecyclerView.ViewHolder(v) {
//
//        val chooseUser = v.findViewById<ConstraintLayout>(R.id.chooseUser)!!
//
//        fun bind(u: User) {
//            v.fullName.text = u.full_name
//
//        }
//    }
//}