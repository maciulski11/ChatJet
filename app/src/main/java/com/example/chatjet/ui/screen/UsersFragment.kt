package com.example.chatjet.ui.screen

import android.annotation.SuppressLint
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatjet.R
import com.example.chatjet.base.BaseFragment
import com.example.chatjet.data.model.User
import com.example.chatjet.services.FirebaseRepository
import com.example.chatjet.ui.adapter.UsersAdapter
import com.example.chatjet.view_model.MainViewModel
import kotlinx.android.synthetic.main.fragment_users.*
import java.util.ArrayList

class UsersFragment: BaseFragment() {
    override val layout: Int = R.layout.fragment_users

    private var usersList = ArrayList<User>()
    private lateinit var adapter: UsersAdapter
    private val viewModel: MainViewModel by activityViewModels()

    @SuppressLint("NotifyDataSetChanged")
    override fun subscribeUi() {

        usersRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        usersRecyclerView.setHasFixedSize(true)

        // We initialize our user list:
        usersList = arrayListOf()

        adapter = UsersAdapter(usersList)
        usersRecyclerView.adapter = adapter

        viewModel.usersList.observe(this) {
            adapter.usersList = it
            adapter.notifyDataSetChanged()
        }

        FirebaseRepository().updateUsersList {
            viewModel.fetchUsers()
        }
    }

    override fun unsubscribeUi() {

    }
}