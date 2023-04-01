package com.example.chatjet.ui.screen

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatjet.R
import com.example.chatjet.base.BaseFragment
import com.example.chatjet.data.model.User
import com.example.chatjet.services.s.repository.FirebaseRepository
import com.example.chatjet.ui.adapter.FindUserAdapter
import com.example.chatjet.view_model.MainViewModel
import kotlinx.android.synthetic.main.fragment_find_user.*

class FindUserFragment: BaseFragment() {
    override val layout: Int = R.layout.fragment_find_user

    private var usersList = ArrayList<User>()
    private lateinit var adapter: FindUserAdapter
    private val viewModel: MainViewModel by activityViewModels()

    override fun subscribeUi() {

        recyclerViewSearch.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewSearch.setHasFixedSize(true)

        usersList = arrayListOf()

        adapter = FindUserAdapter(usersList)
        recyclerViewSearch.adapter = adapter

        viewModel.usersList.observe(this) {
            adapter.usersList = it
            adapter.notifyDataSetChanged()
        }

        FirebaseRepository().updateUsersList {
            viewModel.fetchUsers1()
        }
    }

    override fun unsubscribeUi() {

    }
}