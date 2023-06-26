package com.example.chatjet.models.data

import java.util.*

data class Chat (
    val senderId:String? = null,
    val receiverId:String? = null,
    val message:String? = null,
    val sentAt: Date? = null,
        )