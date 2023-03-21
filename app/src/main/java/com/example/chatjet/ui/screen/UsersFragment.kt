package com.example.chatjet.ui.screen

import android.annotation.SuppressLint
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatjet.R
import com.example.chatjet.base.BaseFragment
import com.example.chatjet.data.model.Friend
import com.example.chatjet.data.model.User
import com.example.chatjet.services.s.repository.FirebaseRepository
import com.example.chatjet.ui.adapter.UsersAdapter
import com.example.chatjet.view_model.MainViewModel
import kotlinx.android.synthetic.main.fragment_users.*
import java.util.ArrayList

class UsersViewModel(var userr: User? = null): ViewModel() {

    private val repository = FirebaseRepository()

    private var user: MutableLiveData<User?> = MutableLiveData()


    private val friendss: ArrayList<Friend>
        get() = user.value?.friends ?: arrayListOf()

    fun fetchUsers() {
        repository.fetchFriends {
            user.postValue(it)
        }
    }

}

    class UsersFragment: BaseFragment() {
        override val layout: Int = R.layout.fragment_users

        private var friendsList = ArrayList<Friend>()
        private lateinit var adapter: UsersAdapter
        val viewModel = UsersViewModel()
        private val mainViewModel: MainViewModel by activityViewModels()

        @SuppressLint("NotifyDataSetChanged")
        override fun subscribeUi() {

            usersRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            usersRecyclerView.setHasFixedSize(true)

            // We initialize our user list:
            friendsList = arrayListOf()

            FirebaseRepository().fetchFriends { it ->
                viewModel.userr = it ?: User()

                viewModel.userr?.friends.let {

                    adapter = UsersAdapter(friendsList, requireView())
                    usersRecyclerView.adapter = adapter

                }



            }

            mainViewModel.friendsList.observe(this) {
                adapter.friendsList = it
                adapter.notifyDataSetChanged()
            }
//            viewModel.fetchUsers()

//            FirebaseRepository().updateUsersList {
//                viewModel.fetchUsers()
//            }
        }

        override fun unsubscribeUi() {

        }
    }