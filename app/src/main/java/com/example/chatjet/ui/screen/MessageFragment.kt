package com.example.chatjet.ui.screen

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.view.View
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatjet.R
import com.example.chatjet.base.BaseFragment
import com.example.chatjet.data.model.Friend
import com.example.chatjet.data.model.FriendsGroup
import com.example.chatjet.services.repository.FirebaseRepository
import com.example.chatjet.ui.activity.OnBackPressedListener
import com.example.chatjet.ui.adapter.MessageAdapter
import com.example.chatjet.view_model.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_message.*
import java.util.*

class MessageFragment : BaseFragment(), OnBackPressedListener {
    override val layout: Int = R.layout.fragment_message

    private lateinit var messageList: ArrayList<Friend>
    private lateinit var adapter: MessageAdapter
    private val mainViewModel: MainViewModel by activityViewModels()

    @SuppressLint("NotifyDataSetChanged", "SuspiciousIndentation")
    override fun subscribeUi() {

        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.visibility = View.VISIBLE

        messageRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        messageRecyclerView.setHasFixedSize(true)

        // We initialize our user list:
        messageList = arrayListOf()

        FirebaseRepository().fetchFriends(FirebaseRepository().currentUserUid) { user ->
            mainViewModel.user = user

            if (user?.firstLogin == true) {
                findNavController().navigate(R.id.action_messageFragment_to_profileEditFragment)
            }

            mainViewModel.users.observe(this) {

                messageList.clear()

                mainViewModel.user?.friends.let { messageOfFriend ->

                    // Real-time list filtering and if uidLastMessage is empty, uid don't load and go to next friend
                    messageOfFriend?.let { it -> messageList.addAll(it.filter { !it.uidLastMessage.isNullOrEmpty() }) }

                    messageList.sortByDescending { it.sentAt }

                    val groupedFriendList = messageList.groupBy { getDateString(it.sentAt!!) }

                    val friendGroupList = mutableListOf<FriendsGroup>()

                    for ((date, friends) in groupedFriendList) {
                        friendGroupList.add(FriendsGroup(date, friends))
                    }

                    adapter = MessageAdapter(
                        messageList,
                        requireView(),
                        { messageUid ->

                            mainViewModel.userReadMessage(messageUid)
                        },
                        { uid ->
                            mainViewModel.deleteChat(uid)

                        },
                        { friendUid, itemView ->

                            fetchFullName(friendUid, itemView)
                        }
                    )

                    messageRecyclerView.adapter = adapter

                    // Check that RecyclerView is empty
                    if (messageRecyclerView.adapter?.itemCount == 0) {
                        // Show TextView if RecyclerView is empty
                        emptyMessageListTV.visibility = View.VISIBLE
                    } else {
                        // Hide TextView, if RecyclerView is not empty
                        emptyMessageListTV.visibility = View.GONE
                    }
                }

                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun fetchFullName(friendUid: String, itemView: View) {

        val fullName = itemView.findViewById<TextView>(R.id.fullName)

        mainViewModel.fetchFriend(friendUid) { friend ->
            fullName.text = friend?.full_name
        }
    }

    private fun getDateString(date: Date): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }

    // This function change option button on visible after login
    override fun onResume() {
        super.onResume()
        requireActivity().invalidateOptionsMenu()
    }

    override fun onBackPressed(): Boolean {
        val navController = findNavController()
        return if (navController.currentDestination?.id == R.id.invitationFragment) {
            navController.navigateUp()
            true
        } else {
            false
        }
    }

    override fun unsubscribeUi() {

    }
}