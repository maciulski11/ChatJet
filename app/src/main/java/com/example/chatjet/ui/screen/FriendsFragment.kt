package com.example.chatjet.ui.screen

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatjet.R
import com.example.chatjet.base.BaseFragment
import com.example.chatjet.data.model.Chat
import com.example.chatjet.data.model.Friend
import com.example.chatjet.data.model.User
import com.example.chatjet.services.s.repository.FirebaseRepository
import com.example.chatjet.ui.adapter.FriendsAdapter
import com.example.chatjet.view_model.MainViewModel
import kotlinx.android.synthetic.main.fragment_friends.*
import kotlinx.android.synthetic.main.item_user.*
import java.util.*

class FriendsViewModel(var user: User? = null) : ViewModel() {

    var userrr: MutableLiveData<User?> = MutableLiveData(null)

}

class UsersFragment : BaseFragment() {
    override val layout: Int = R.layout.fragment_friends

    private lateinit var friendsList: ArrayList<Friend>
    private lateinit var adapter: FriendsAdapter
    private val viewModel = FriendsViewModel()
    private val mainViewModel: MainViewModel by activityViewModels()

    @SuppressLint("NotifyDataSetChanged")
    override fun subscribeUi() {

        friendsRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        friendsRecyclerView.setHasFixedSize(true)

        // We initialize our user list:
        friendsList = arrayListOf()

            FirebaseRepository().fetchFriends(FirebaseRepository().currentUserUid!!) { user ->
                viewModel.user = user ?: User()

                viewModel.userrr.observe(this) {

                    friendsList.clear()

                    viewModel.user?.friends.let { friend ->

                        friendsList.addAll(friend!!)


//
////                // Sort the chatList by timestamp in descending order
////                friendsList.sortByDescending { it.sentAt }
////
////                // Group the chatList by date
////                val groupedFriendList =
////                    friendsList.groupBy { it.sentAt?.let { it1 -> getDateString(it1) } }
////
////                // Create a new list to display chat groups
////                val friendGroupList = mutableListOf<FriendsGroup>()
////
////                // Iterate through the groupedChatList and create ChatGroup objects
////                for ((date, friends) in groupedFriendList) {
////                    friendGroupList.add(FriendsGroup(date.toString(), friends))
////                }


                        adapter = FriendsAdapter(
                            friendsList,
                            requireView()
                        )

                        friendsRecyclerView.adapter = adapter
                    }
                    adapter.notifyDataSetChanged()
                }
        }

        findUser.setOnClickListener {
            findNavController().navigate(R.id.action_usersFragment_to_findUserFragment)
        }
    }

    private fun getDateString(date: Date): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }

    override fun unsubscribeUi() {

    }
}