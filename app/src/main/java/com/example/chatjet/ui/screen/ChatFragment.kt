package com.example.chatjet.ui.screen

import com.example.chatjet.R
import com.example.chatjet.base.BaseFragment

class ChatFragment: BaseFragment() {
    override val layout: Int = R.layout.fragment_chat

    override fun subscribeUi() {

        // Wczytanie elementów w recycler view od dołu
//        val layoutManager = LinearLayoutManager(this)
//        layoutManager.stackFromEnd = true
//        chatRecyclerView.layoutManager = layoutManager
    }

    override fun unsubscribeUi() {

    }


}