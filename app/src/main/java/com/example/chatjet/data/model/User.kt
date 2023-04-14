package com.example.chatjet.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class User (
    val email: String? = null,
    val uid: String? = null,
    val token: String? = null,
    val full_name: String? = null,
    val photo: String? = null,
    val number: Int? = null,
    val location: String? = null,
    val status: Boolean? = null,
    val friends: @RawValue ArrayList<Friend>? = arrayListOf()
): Parcelable {}