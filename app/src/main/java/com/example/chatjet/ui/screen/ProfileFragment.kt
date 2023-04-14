package com.example.chatjet.ui.screen

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.chatjet.R
import com.example.chatjet.base.BaseFragment
import com.example.chatjet.services.s.repository.FirebaseRepository
import com.example.chatjet.view_model.MainViewModel
import com.google.firebase.database.collection.LLRBNode.Color
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment: BaseFragment() {
    override val layout: Int = R.layout.fragment_profile

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun subscribeUi() {

        editProfileButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_profileEditFragment)
        }

        fetchProfileData()
    }

    private fun fetchProfileData() {
        mainViewModel.fetchUserOrFriend(FirebaseRepository().currentUserUid ?: "") { user ->
            fullNameTV.text = user?.full_name
            phoneNumberTV.text = user?.number.toString()
            locationTV.text = user?.location

            if (user?.status == true) {
                statusText.text = "Active"
                statusColor.setColorFilter(android.graphics.Color.GREEN)
            } else {
                statusText.text = "Inactive"
                statusColor.setColorFilter(android.graphics.Color.RED)
            }

            Glide.with(requireView())
                .load(user?.photo)
                .override(440, 440)
                .circleCrop()
                .into(photoProfile)
        }
    }

    override fun unsubscribeUi() {

    }
}