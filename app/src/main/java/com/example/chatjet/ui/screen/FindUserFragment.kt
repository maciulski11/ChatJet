package com.example.chatjet.ui.screen

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatjet.R
import com.example.chatjet.base.BaseFragment
import com.example.chatjet.data.model.User
import com.example.chatjet.services.utils.ToastUtils
import com.example.chatjet.ui.activity.OnBackPressedListener
import com.example.chatjet.ui.adapter.FindUserAdapter
import com.example.chatjet.view_model.FindUserViewModel
import com.example.chatjet.view_model.MainViewModel
import kotlinx.android.synthetic.main.fragment_find_user.*
import java.util.*
import kotlin.collections.ArrayList

class FindUserFragment : BaseFragment(), OnBackPressedListener {
    override val layout: Int = R.layout.fragment_find_user

    private var usersList = ArrayList<User>()
    private lateinit var adapter: FindUserAdapter
    private val viewModel: FindUserViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    @SuppressLint("NotifyDataSetChanged")
    override fun subscribeUi() {

        recyclerViewSearch.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewSearch.setHasFixedSize(true)

        usersList = arrayListOf()

        adapter = FindUserAdapter(
            usersList,
            requireContext(),
            { userUid, itemView ->

                fetchUser(userUid, itemView)
            },
            { userUid ->

                sendInvitation(userUid)
            })

        recyclerViewSearch.adapter = adapter

        viewModel.usersList.observe(this) {
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

        viewModel.fetchUsersList()

    }

    private fun fetchUser(userUid: String, itemView: View) {

        val photo = itemView.findViewById<ImageView>(R.id.photo)
        val nameUser = itemView.findViewById<TextView>(R.id.nameUser)
        val location = itemView.findViewById<TextView>(R.id.locationTV)

        mainViewModel.fetchFriend(userUid) { user ->

            nameUser.text = user?.full_name
            location.text = user?.location

            if (user?.photo.isNullOrEmpty()) {

                Glide.with(requireContext())
                    .load(R.drawable.ic_baseline_account_circle_24)
                    .circleCrop()
                    .into(photo)

            } else {

                Glide.with(requireContext())
                    .load(user?.photo)
                    .circleCrop()
                    .into(photo)
            }
        }
    }

    private fun sendInvitation(uid: String) {
        viewModel.sendInvitation(uid)

        ToastUtils.showToast(
            "Invitation sent!",
            R.drawable.ic_baseline_check_circle_outline_24,
            R.color.green,
            Toast.LENGTH_SHORT
        )
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
        // Unsubscribes from observing the invitations list LiveData in the ViewModel
        viewModel.usersList.removeObservers(viewLifecycleOwner)
    }
}