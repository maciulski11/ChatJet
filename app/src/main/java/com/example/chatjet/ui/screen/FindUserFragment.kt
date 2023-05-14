package com.example.chatjet.ui.screen

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatjet.R
import com.example.chatjet.base.BaseFragment
import com.example.chatjet.data.model.User
import com.example.chatjet.services.repository.FirebaseRepository
import com.example.chatjet.services.utils.Utilities
import com.example.chatjet.ui.adapter.FindUserAdapter
import com.example.chatjet.view_model.MainViewModel
import kotlinx.android.synthetic.main.fragment_find_user.*
import java.util.*
import kotlin.collections.ArrayList

class FindUserFragment : BaseFragment() {
    override val layout: Int = R.layout.fragment_find_user

    private var usersList = ArrayList<User>()
    private lateinit var adapter: FindUserAdapter
    private val mainViewModel: MainViewModel by activityViewModels()

    @SuppressLint("NotifyDataSetChanged")
    override fun subscribeUi() {

        recyclerViewSearch.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewSearch.setHasFixedSize(true)

        usersList = arrayListOf()

        adapter = FindUserAdapter(usersList, requireContext())
        {
            mainViewModel.sendInvitation(it)

            Utilities.customToast(
                requireContext(),
                "Invitation sent!",
                R.drawable.ic_baseline_check_circle_outline_24,
                R.color.white,
                R.color.green,
                Toast.LENGTH_SHORT
            )
        }
        recyclerViewSearch.adapter = adapter

        mainViewModel.usersList.observe(this) {
            adapter.usersList = it
            adapter.notifyDataSetChanged()

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {

                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {

                    if (newText == "") {
                        adapter.setFilteredList(it)
                    }
                    filterList(newText)

                    return true
                }
            })
        }

        FirebaseRepository().updateUsersList {
            mainViewModel.fetchUsers()
        }

    }

    private fun filterList(query: String?) {
        val filteredList = ArrayList<User>()
        if (query != null) {
            for (i in adapter.usersList) {
                if (i.full_name?.lowercase(Locale.ROOT)!!.contains(query)) {
                    filteredList.add(i)
                }
            }

            if (filteredList.isEmpty()) {
                Toast.makeText(requireContext(), "No Data found", Toast.LENGTH_SHORT).show()
            } else {
                adapter.setFilteredList(filteredList)
            }
        }
    }

    override fun unsubscribeUi() {
        mainViewModel.usersList.removeObservers(this)
    }
}