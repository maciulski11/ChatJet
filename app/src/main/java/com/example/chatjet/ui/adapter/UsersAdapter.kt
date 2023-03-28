package com.example.chatjet.ui.adapter

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.os.Bundle
import android.util.Log
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
import com.example.chatjet.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.item_user.view.*

class UsersAdapter(private val photo: String?, var friendsList: ArrayList<Friend>, private val v: View): RecyclerView.Adapter<UsersAdapter.MyViewHolder>() {

    private val fbAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val currentUserUid: String?
        get() = fbAuth.currentUser?.uid

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_user, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val friend = friendsList[position]

        val uid = friend.uid
        val db = FirebaseFirestore.getInstance()
        val usersRef = db.collection("users")
        val query = usersRef.whereEqualTo("uid", uid)

        query.get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val user = querySnapshot.documents[0].toObject(User::class.java)
                // Ustaw tutaj dane użytkownika w widoku lub przekaż dalej do innej części aplikacji.
                val icon = holder.itemView.findViewById<ImageView>(R.id.userPhoto)
                Glide.with(holder.itemView)
                    .load(user?.photo)
                    .override(220,220)
                    .circleCrop()
                    .into(icon)

            } else {
                // Użytkownik nie został znaleziony w Firestore.
            }
        }.addOnFailureListener { exception ->
            // Obsługa błędów.
        }

//        val icon = holder.itemView.findViewById<ImageView>(R.id.userPhoto)
//        Glide.with(holder.itemView)
//            .load(photo)
//            .override(220,220)
//            .circleCrop()
//            .into(icon)

        holder.chooseUser.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable(
                "friend",
                Friend(
                   friend.uid,
                    "",
                    friend.readMessage,
                    "",
                    friend.token
                )
            )
            bundle.putInt("friendIndex", position) // <-- dodaj ten wiersz, aby przekazać indeks użytkownika
            v.findNavController().navigate(R.id.action_usersFragment_to_chatFragment, bundle)
        }

        holder.bind(friend)
    }

    override fun getItemCount(): Int = friendsList.size

    inner class MyViewHolder(private var view: View) : RecyclerView.ViewHolder(view) {

        val chooseUser = view.findViewById<ConstraintLayout>(R.id.chooseUser)!!

        @SuppressLint("SimpleDateFormat")
        fun bind(friend: Friend) {
            // SimpleDateFormat("dd.MM.yyyy HH:mm")
            val dateFormat = SimpleDateFormat("HH:mm")

            view.fullName.text = friend.fullName
            view.lastMessage.text = friend.lastMessage
            view.time.text = dateFormat.format(friend.sentAt).toString()

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