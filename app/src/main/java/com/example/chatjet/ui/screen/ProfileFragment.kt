package com.example.chatjet.ui.screen

import android.content.Context
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.chatjet.R
import com.example.chatjet.base.BaseFragment
import com.example.chatjet.services.repository.FirebaseRepository
import com.example.chatjet.services.utils.AnimationUtils
import com.example.chatjet.ui.activity.OnBackPressedListener
import com.example.chatjet.models.view_model.MainViewModel
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.editProfileButton
import kotlinx.android.synthetic.main.fragment_profile.statusColor
import kotlinx.android.synthetic.main.fragment_profile.statusText

class ProfileFragment : BaseFragment(), OnBackPressedListener {
    override val layout: Int = R.layout.fragment_profile

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun subscribeUi() {

        friendsList.setOnClickListener {
            findNavController().navigate(
                R.id.action_profileFragment_to_friendsFragment,
                null,
                AnimationUtils.topNavAnim
            )
        }

        editProfileButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_profileFragment_to_profileEditFragment,
                null,
                AnimationUtils.rightNavAnim
            )
        }

        logoutButton.setOnClickListener {

            val sharedPrefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            sharedPrefs.edit().putString("myArgument", "xx").apply()

            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
            activity?.finish()
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
