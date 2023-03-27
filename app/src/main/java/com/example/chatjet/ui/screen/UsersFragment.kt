package com.example.chatjet.ui.screen

import android.annotation.SuppressLint
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatjet.R
import com.example.chatjet.base.BaseFragment
import com.example.chatjet.data.model.Friend
import com.example.chatjet.data.model.User
import com.example.chatjet.services.s.repository.FirebaseRepository
import com.example.chatjet.ui.adapter.UsersAdapter
import com.example.chatjet.view_model.MainViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_users.*
import kotlinx.android.synthetic.main.item_user.*
import java.util.ArrayList

class UsersViewModel(var user: User? = null) : ViewModel() {


}

class UsersFragment : BaseFragment() {
    override val layout: Int = R.layout.fragment_users

    private lateinit var friendsList: ArrayList<Friend>
    private lateinit var adapter: UsersAdapter
    val viewModel = UsersViewModel()
    private val mainViewModel: MainViewModel by activityViewModels()

    @SuppressLint("NotifyDataSetChanged")
    override fun subscribeUi() {

        usersRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        usersRecyclerView.setHasFixedSize(true)

        // We initialize our user list:
        friendsList = arrayListOf()

        FirebaseRepository().fetchFriends { user ->
            viewModel.user = user ?: User()

            viewModel.user?.friends.let { friend ->

                friend?.let { it -> friendsList.addAll(it) }
                adapter = UsersAdapter(
                    user.photo,
                    friendsList,
                    requireView()
                )
                usersRecyclerView.adapter = adapter

            }


        }

        //nie dziala
//            mainViewModel.friendsList.observe(this) {
//                adapter.friendsList = it
//                adapter.notifyDataSetChanged()
//            }

    }

    override fun unsubscribeUi() {

    }
}