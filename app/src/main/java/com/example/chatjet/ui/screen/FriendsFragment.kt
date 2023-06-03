package com.example.chatjet.ui.screen

import android.annotation.SuppressLint
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatjet.R
import com.example.chatjet.base.BaseFragment
import com.example.chatjet.data.model.Friend
import com.example.chatjet.services.repository.FirebaseRepository
import com.example.chatjet.ui.adapter.FriendsAdapter
import com.example.chatjet.view_model.MainViewModel
import kotlinx.android.synthetic.main.fragment_friends.*
import kotlinx.android.synthetic.main.fragment_message.*
import java.util.ArrayList

class FriendsFragment: BaseFragment() {
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
                        requireView()
                    )

                    friendsRecyclerView.adapter = adapter
                }
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun unsubscribeUi() {

    }
}