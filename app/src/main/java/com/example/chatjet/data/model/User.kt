package com.example.chatjet.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User (
    val email: String? = null,
    val uid: String? = null,
    val token: String? = null,
    val full_name: String? = null,
    val photo: String? = null,
    val message: String? = null
): Parcelable {}