package com.example.chatjet.data.model

import java.util.*

data class Chat (
    val senderId:String? = null,
    val receiverId:String? = null,
    val message:String? = null,
    val sentAt: Date? = null,
        )