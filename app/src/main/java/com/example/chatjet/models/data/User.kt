package com.example.chatjet.models.data

import android.os.Parcelable
import com.example.chatjet.models.data.Friend
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
    var firstLogin: Boolean? = null,
    val location: String? = null,
    var status: Boolean? = null,
    val friends: @RawValue ArrayList<Friend>? = arrayListOf()
): Parcelable {}