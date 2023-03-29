package com.example.chatjet.ui.screen

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatjet.R
import com.example.chatjet.base.BaseFragment
import com.example.chatjet.data.model.Friend
import com.example.chatjet.data.model.FriendsGroup
import com.example.chatjet.data.model.User
import com.example.chatjet.services.s.repository.FirebaseRepository
import com.example.chatjet.ui.adapter.UsersAdapter
import com.example.chatjet.view_model.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_users.*
import kotlinx.android.synthetic.main.item_user.*
import java.util.*

class UsersViewModel(var user: User? = null, var friend: Friend? = null) : ViewModel() {


}

class UsersFragment : BaseFragment() {
    override val layout: Int = R.layout.fragment_users

    private lateinit var friendsList: ArrayList<Friend>
    private lateinit var adapter: UsersAdapter
    val viewModel = UsersViewModel()
    private val mainViewModel: MainViewModel by activityViewModels()

    private val fbAuth = FirebaseAuth.getInstance()

    val currentUserUid: String?
        get() = fbAuth.currentUser?.uid

    @SuppressLint("NotifyDataSetChanged")
    override fun subscribeUi() {

        usersRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        usersRecyclerView.setHasFixedSize(true)

        // We initialize our user list:
        friendsList = arrayListOf()

        FirebaseRepository().fetchFriends(currentUserUid!!) { user ->
            viewModel.user = user ?: User()

            viewModel.user?.friends.let { friend ->

                friend?.let { it -> friendsList.addAll(it) }

//                // Sort the chatList by timestamp in descending order
//                friendsList.sortByDescending { it.sentAt }
//
//                // Group the chatList by date
//                val groupedFriendList =
//                    friendsList.groupBy { it.sentAt?.let { it1 -> getDateString(it1) } }
//
//                // Create a new list to display chat groups
//                val friendGroupList = mutableListOf<FriendsGroup>()
//
//                // Iterate through the groupedChatList and create ChatGroup objects
//                for ((date, friends) in groupedFriendList) {
//                    friendGroupList.add(FriendsGroup(date.toString(), friends))
//                }

                adapter = UsersAdapter(
                    friendsList,
                    requireView()
                )
                usersRecyclerView.adapter = adapter

            }


        }

    }

    private fun getDateString(date: Date): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }

    override fun unsubscribeUi() {

    }
}