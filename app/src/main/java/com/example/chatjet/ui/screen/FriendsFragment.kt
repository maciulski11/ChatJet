package com.example.chatjet.ui.screen

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatjet.R
import com.example.chatjet.base.BaseFragment
import com.example.chatjet.data.model.Chat
import com.example.chatjet.data.model.Friend
import com.example.chatjet.data.model.FriendsGroup
import com.example.chatjet.data.model.User
import com.example.chatjet.services.s.repository.FirebaseRepository
import com.example.chatjet.ui.activity.OnBackPressedListener
import com.example.chatjet.ui.adapter.FriendsAdapter
import com.example.chatjet.view_model.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_friends.*
import java.util.*

class FriendsViewModel(var user: User? = null, val date: Chat? = null) : ViewModel() {

    var userrr: MutableLiveData<User?> = MutableLiveData(null)

}

class FriendsFragment : BaseFragment(), OnBackPressedListener {
    override val layout: Int = R.layout.fragment_friends

    private lateinit var friendsList: ArrayList<Friend>
    private lateinit var adapter: FriendsAdapter
    private val viewModel = FriendsViewModel()
    private val mainViewModel: MainViewModel by activityViewModels()

    @SuppressLint("NotifyDataSetChanged", "SuspiciousIndentation")
    override fun subscribeUi() {

        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.visibility = View.VISIBLE

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


                    friendsList.sortByDescending { it.sentAt }


                    val groupedFriendList = friendsList.groupBy { getDateString(it.sentAt!!) }

                    val friendGroupList = mutableListOf<FriendsGroup>()

                    for ((date, friends) in groupedFriendList) {
                        friendGroupList.add(FriendsGroup(date, friends))
                    }

                    adapter = FriendsAdapter(
                        friendsList,
                        requireView()
                    )

                    friendsRecyclerView.adapter = adapter
                }
                adapter.notifyDataSetChanged()
            }
        }
    }


    private fun getDateString(date: Date): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }

    // this function change option button on visible after login
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