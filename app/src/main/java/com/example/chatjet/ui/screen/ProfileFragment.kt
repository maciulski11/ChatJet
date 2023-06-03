package com.example.chatjet.ui.screen

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.chatjet.R
import com.example.chatjet.base.BaseFragment
import com.example.chatjet.services.repository.FirebaseRepository
import com.example.chatjet.services.utils.AnimationUtils
import com.example.chatjet.view_model.MainViewModel
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.editProfileButton
import kotlinx.android.synthetic.main.fragment_profile.statusColor
import kotlinx.android.synthetic.main.fragment_profile.statusText

class ProfileFragment : BaseFragment() {
    override val layout: Int = R.layout.fragment_profile

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun subscribeUi() {

        friendsList.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_friendsFragment, null , AnimationUtils.topNavAnim)
        }

        editProfileButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_profileEditFragment, null, AnimationUtils.rightNavAnim)
        }

        fetchProfileData()
    }

    private fun fetchProfileData() {
        mainViewModel.fetchUserOrFriend(FirebaseRepository().currentUserUid) { user ->
            fullNameTV.text = user?.full_name
            phoneNumberTV.text = user?.number.toString()
            locationTV.text = user?.location
            amountFriendsTV.text = user?.friends?.size.toString()

            if (user?.status == true) {
                statusText.text = "Active"
                statusColor.setColorFilter(android.graphics.Color.GREEN)
            } else {
                statusText.text = "Not active"
                statusColor.setColorFilter(android.graphics.Color.RED)
            }

            if (user?.photo!!.isEmpty()) {
                photoProfile.setImageResource(R.drawable.ic_baseline_account_circle_200)

            } else {

                Glide.with(requireView())
                    .load(user.photo)
                    .override(450, 450)
                    .circleCrop()
                    .into(photoProfile)
            }
        }
    }

    override fun unsubscribeUi() {

    }
}