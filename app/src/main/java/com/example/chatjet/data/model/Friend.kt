package com.example.chatjet.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Friend (
    val uid: String? = null,
    val lastMessage: String? = null,
    val readMessage: String? = null,
    val fullName: String? = null,
    val token: String? = null,
    val sentAt: Date? = null,
    ) : Parcelable