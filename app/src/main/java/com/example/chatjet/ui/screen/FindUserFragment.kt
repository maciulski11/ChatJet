package com.example.chatjet.ui.screen

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatjet.R
import com.example.chatjet.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_find_user.*

class FindUserFragment: BaseFragment() {
    override val layout: Int = R.layout.fragment_find_user

    private val usersList 

    override fun subscribeUi() {

        recyclerViewSearch.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewSearch.setHasFixedSize(true)
    }

    override fun unsubscribeUi() {

    }
}