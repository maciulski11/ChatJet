package com.example.chatjet.ui.screen

import androidx.navigation.fragment.findNavController
import com.example.chatjet.R
import com.example.chatjet.base.BaseFragment

class LoginFragment: BaseFragment() {
    override val layout: Int = R.layout.fragment_login

    override fun subscribeUi() {

//        loginBT.setOnClickListener {
//            findNavController().navigate(R.id.action_loginFragment_to_usersFragment)
//        }
    }

    override fun unsubscribeUi() {

    }
}