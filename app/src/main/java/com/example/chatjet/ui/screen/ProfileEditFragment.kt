package com.example.chatjet.ui.screen

import android.view.View
import com.example.chatjet.R
import com.example.chatjet.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_profile_edit.*

class ProfileEditFragment : BaseFragment() {
    override val layout: Int = R.layout.fragment_profile_edit

    override fun subscribeUi() {

        checkNumberButton.setOnClickListener {

            confirmNumber.visibility = View.VISIBLE
            confirmNumberView.visibility = View.VISIBLE
        }

        acceptStatusButton.setOnClickListener {

            acceptedStatusButton.visibility = View.VISIBLE
            acceptStatusButton.visibility = View.GONE
        }

    }

    override fun unsubscribeUi() {

    }
}