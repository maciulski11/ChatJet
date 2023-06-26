package com.example.chatjet.ui.adapter

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.chatjet.R
import com.example.chatjet.models.data.Friend
import com.example.chatjet.services.repository.FirebaseRepository
import com.example.chatjet.services.utils.AnimationUtils

class FriendsAdapter(
    var friendsList: ArrayList<Friend>,
    private val context: Context,
    private val v: View,
    val onCallFriend: (String) -> Unit,
    val onDeleteFriend: (Friend) -> Unit,
    ) :
    RecyclerView.Adapter<FriendsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsAdapter.MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_friend, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FriendsAdapter.MyViewHolder, position: Int) {
        val friend = friendsList[position]

        holder.bind(friend)
    }

    override fun getItemCount(): Int = friendsList.size

    inner class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        private val name = view.findViewById<TextView>(R.id.nameUser)
        private val callButton = view.findViewById<ImageButton>(R.id.callButton)
        private val messageButton = view.findViewById<ImageButton>(R.id.messageButton)
        private val deleteButton = view.findViewById<ImageButton>(R.id.deleteButton)

        fun bind(friend: Friend) {

            FirebaseRepository().fetchFriends(friend.uid ?: "") { f ->

                name.text = f?.full_name

                callButton.setOnClickListener {

                    onCallFriend(f?.number.toString())
                }

                messageButton.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putParcelable(
                        "friend",
                        Friend(
                            friend.uid
                        )
                    )

                    v.findNavController().navigate(
                        R.id.action_friendsFragment_to_chatFragment,
                        bundle,
                        AnimationUtils.downNavAnim
                    )
                }

                deleteButton.setOnClickListener {

                    val alertDialog = AlertDialog.Builder(context)
                        .setTitle("Delete friend")
                        .setMessage("Are you sure you want to delete this friend?")
                        .setPositiveButton("Yes") { _, _ ->
                            //TODO: zrobic zeby po usunieciu przyjaciela tez usunely sie wszystkie wiadomosci
                            // Delete friend
                            onDeleteFriend(friend)
                        }
                        .setNegativeButton("No") { _, _ ->

                        }
                        .create()

                    alertDialog.setCanceledOnTouchOutside(false)
                    alertDialog.show()

//                    // usunięcie aktualnie zalogowanego użytkownika z listy przyjaciół usuniętego użytkownika
//                    db.collection(FirebaseRepository.USERS).document(friend.uid!!)
//                        .get()
//                        .addOnSuccessListener { document ->
//                            if (document != null) {
//                                val friends = document.get("friends") as ArrayList<String>
//                                friends.remove(FirebaseRepository().currentUserUid!!)
//                                db.collection(FirebaseRepository.USERS).document(friend.uid)
//                                    .update("friends", friends)
//                                    .addOnSuccessListener {
//                                        Log.d("TAG", "Current user successfully removed from friend's list!")
//                                    }
//                                    .addOnFailureListener { e ->
//                                        Log.e("TAG", "Error removing current user from friend's list", e)
//                                    }
//                            } else {
//                                Log.d("TAG", "No such document")
//                            }
//                        }
//                        .addOnFailureListener { e ->
//                            Log.e("TAG", "Error getting document", e)
//                        }
                }
            }
        }
    }
}