package com.example.chatjet.ui.adapter

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.chatjet.R
import com.example.chatjet.data.model.Friend
import com.example.chatjet.services.s.repository.FirebaseRepository

class FriendsAdapter(var friendsList: ArrayList<Friend>, private val v: View, private val context: Context) :
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

    inner class MyViewHolder(private var view: View) : RecyclerView.ViewHolder(view) {

        private val name = view.findViewById<TextView>(R.id.nameUser)
        private val callButton = view.findViewById<ImageButton>(R.id.callButton)

        fun bind(friend: Friend) {

            FirebaseRepository().fetchFriends(friend.uid ?: "") { f ->

                name.text = f.full_name

                callButton.setOnClickListener {
                    val phoneNumber = f.number
                    // ACTION_DIAL - przenosi do edycji numeru przed polaczeniem
                    val callIntent = Intent(Intent.ACTION_CALL)
                    callIntent.data = Uri.parse("tel:$phoneNumber")
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(context, "Brak uprawnień do dzwonienia", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    context.startActivity(callIntent)
                }
            }
        }
    }
}