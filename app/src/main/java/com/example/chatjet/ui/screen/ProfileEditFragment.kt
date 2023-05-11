package com.example.chatjet.ui.screen

import android.graphics.Color
import android.util.Log
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.chatjet.R
import com.example.chatjet.base.BaseFragment
import com.example.chatjet.services.s.repository.FirebaseRepository
import com.example.chatjet.view_model.MainViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import kotlinx.android.synthetic.main.fragment_profile_edit.statusColor
import kotlinx.android.synthetic.main.fragment_profile_edit.statusText

class ProfileEditFragment : BaseFragment() {
    override val layout: Int = R.layout.fragment_profile_edit

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun subscribeUi() {

        mainViewModel.users.observe(this) {

            FirebaseRepository().fetchFriends(FirebaseRepository().currentUserUid.toString()) { user ->


                phoneNumberET?.hint = user.number.toString()
                locationET?.hint = user.location
                fullNameET?.hint = user.full_name

                if (user.status == true) {
                    statusText?.text = "Active"
                    statusColor?.setColorFilter(Color.GREEN)

                } else {
                    statusText?.text = "Not active"
                    statusColor?.setColorFilter(Color.RED)
                }

                if (user.photo!!.isEmpty()) {
                    photoProfileButton?.setImageResource(R.drawable.ic_baseline_account_circle_200)

                } else {
                    Glide.with(this)
                        .load(user.photo ?: "")
                        .circleCrop()
                        .override(450, 450)
                        .into(photoProfileButton)
                }
            }
        }

        changeStatusButton.setOnClickListener {
            if (statusText.text == "Not active") {
                statusText.text = "Active"
                statusColor.setColorFilter(Color.GREEN)

            } else {
                statusText.text = "Not active"
                statusColor.setColorFilter(Color.RED)

            }
        }

        saveButton.setOnClickListener {

            val name = fullNameET.text.toString()
            val number = phoneNumberET.text.toString().toInt()
            val location = locationET.text.toString()

            mainViewModel.updateDataOfUser(name, number, location, findNavController())

            fullNameET.setText("")
            phoneNumberET.setText("")
            locationET.setText("")

        }

    }


    override fun unsubscribeUi() {

    }
}