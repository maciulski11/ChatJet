package com.example.chatjet.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatjet.R
import com.example.chatjet.base.BaseFragment
import com.example.chatjet.models.data.Friend
import com.example.chatjet.services.repository.FirebaseRepository
import com.example.chatjet.ui.adapter.FriendsAdapter
import com.example.chatjet.models.view_model.MainViewModel
import kotlinx.android.synthetic.main.fragment_friends.*
import kotlinx.android.synthetic.main.fragment_message.*
import java.util.ArrayList

class FriendsFragment : BaseFragment() {
    override val layout: Int = R.layout.fragment_friends

    private lateinit var friendsList: ArrayList<Friend>
    private lateinit var adapter: FriendsAdapter
    private val mainViewModel: MainViewModel by activityViewModels()

    @SuppressLint("NotifyDataSetChanged")
    override fun subscribeUi() {

        friendsRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        friendsRecyclerView.setHasFixedSize(true)

        // We initialize our user list:
        friendsList = arrayListOf()

        FirebaseRepository().fetchFriends(FirebaseRepository().currentUserUid) { user ->
            mainViewModel.user = user

            mainViewModel.users.observe(this) {

                friendsList.clear()

                mainViewModel.user?.friends.let { friend ->

                    friendsList.addAll(friend!!)

                    adapter = FriendsAdapter(
                        friendsList,
                        requireContext(),
                        requireView(),
                        { phoneNumber ->
                            callFriend(phoneNumber)
                        },
                        { deleteFriend ->
                            mainViewModel.deleteFriend(deleteFriend)
                        })

                    friendsRecyclerView.adapter = adapter
                }

                if (friendsList.isEmpty()) {
                    emptyFriendsListTV.visibility = View.VISIBLE
                } else {
                    emptyFriendsListTV.visibility = View.GONE
                }
            }

            adapter.notifyDataSetChanged()
        }
    }

    private fun callFriend(phoneNumber: String) {
        // ACTION_DIAL - przenosi do edycji numeru przed polaczeniem
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:$phoneNumber")
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(context, "Brak uprawnień do dzwonienia", Toast.LENGTH_SHORT)
                .show()
            return
        }
        context?.startActivity(callIntent)
    }

    override fun unsubscribeUi() {

    }
}