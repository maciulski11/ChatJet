package com.example.chatjet.models.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Friend (
    val uid: String? = null,
    var uidLastMessage: String? = null,
    val readMessage: Boolean? = null,
    var sentAt: Date? = null
) : Parcelable