package com.example.chatjet.ui.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatjet.R
import com.example.chatjet.data.model.Friend
import com.example.chatjet.data.model.User
import com.example.chatjet.services.s.repository.FirebaseRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.item_find_user.view.*

class FindUserAdapter(var usersList: ArrayList<User>):
    RecyclerView.Adapter<FindUserAdapter.MyViewHolder>() {

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

            inviteButton.setOnClickListener{

                val dataReceived = hashMapOf(
                    "uid" to FirebaseRepository().currentUserUid!!,
                    "accept" to false
                )

                val db = FirebaseFirestore.getInstance()
                db.collection(FirebaseRepository.USERS).document(user.uid ?: "")
                    .collection(FirebaseRepository.INVITATIONS_RECEIVED).document(FirebaseRepository().currentUserUid!!)
                    .set(dataReceived)
                    .addOnSuccessListener {
                        Log.d("TAG", "DocumentSnapshot successfully written!")
                    }
                    .addOnFailureListener { e ->
                        Log.w("TAG", "Error writing document", e)
                    }

                val dataSent = hashMapOf(
                    "uid" to user.uid,
                    "accept" to false
                )

                db.collection(FirebaseRepository.USERS).document(FirebaseRepository().currentUserUid!!)
                    .collection(FirebaseRepository.INVITATIONS_SENT).document(user.uid ?: "")
                    .set(dataSent)
                    .addOnSuccessListener {
                        Log.d("TAG", "DocumentSnapshot successfully written!")
                    }
                    .addOnFailureListener { e ->
                        Log.w("TAG", "Error writing document", e)
                    }
            }

        }
    }
}