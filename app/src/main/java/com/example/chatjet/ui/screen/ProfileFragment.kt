package com.example.chatjet.ui.screen

import androidx.navigation.fragment.findNavController
import com.example.chatjet.R
import com.example.chatjet.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment: BaseFragment() {
    override val layout: Int = R.layout.fragment_profile

    override fun subscribeUi() {

        editProfileButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_profileEditFragment)
        }

    }

    override fun unsubscribeUi() {

    }
}