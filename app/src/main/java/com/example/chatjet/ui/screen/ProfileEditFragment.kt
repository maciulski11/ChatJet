package com.example.chatjet.ui.screen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.chatjet.R
import com.example.chatjet.base.BaseFragment
import com.example.chatjet.services.repository.FirebaseRepository
import com.example.chatjet.services.utils.Utilities
import com.example.chatjet.view_model.MainViewModel
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import kotlinx.android.synthetic.main.fragment_profile_edit.statusColor
import kotlinx.android.synthetic.main.fragment_profile_edit.statusText
import java.io.ByteArrayOutputStream

class ProfileEditFragment : BaseFragment() {
    override val layout: Int = R.layout.fragment_profile_edit

    private val PROFILE_DEBUG = "TAKE PHOTO"
    private val REQUEST_IMAGE_CAPTURE = 1

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun subscribeUi() {

        FirebaseRepository().fetchFriends(FirebaseRepository().currentUserUid) { user ->

            mainViewModel.users.observe(this) {

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

            // We check that user enters 9 digits of phone number or the field is empty
            if (phoneNumberET.text.isEmpty() || phoneNumberET.text.matches(Regex("^\\d{9}$"))) {
                val name = fullNameET.text.toString()
                val number = phoneNumberET.text.toString()
                val location = locationET.text.toString()

                mainViewModel.updateDataOfUser(name, number, location, findNavController())

                fullNameET.setText("")
                phoneNumberET.setText("")
                locationET.setText("")
            } else {

                Utilities.customToast(
                    requireContext(),
                    "Your phone number should have 9 digits!",
                    R.drawable.ic_baseline_remove_circle_outline_24,
                    Toast.LENGTH_SHORT
                )
            }
        }

        setupTakePictureClick()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap

            Log.d(PROFILE_DEBUG, "BITMAP: " + imageBitmap.byteCount.toString())

            Glide.with(this)
                .load(imageBitmap)
                .circleCrop()
                .override(450, 450)
                .into(photoProfileButton)

            val stream = ByteArrayOutputStream()
            val result = imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
            val byteArray = stream.toByteArray()

            if (result) mainViewModel.uploadUserPhoto(byteArray)
        }
    }

    private fun setupTakePictureClick() {
        //funkcja ktora odpowiada za zrobienie zdjecia po klikniecu w nasz imagebutton
        photoProfileButton.setOnClickListener {

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE)
            try {
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            } catch (exc: Exception) {
                Log.d(PROFILE_DEBUG, exc.message.toString())
            }
        }
    }

    override fun unsubscribeUi() {

    }
}