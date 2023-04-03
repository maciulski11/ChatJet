package com.example.chatjet.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Friend (
    val uid: String? = null,
    val uidLastMessage: String? = null,
    val readMessage: Boolean? = null,
    ) : Parcelable