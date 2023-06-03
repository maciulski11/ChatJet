package com.example.chatjet.ui.screen

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.chatjet.R
import com.example.chatjet.base.BaseFragment
import com.example.chatjet.services.repository.FirebaseRepository
import com.example.chatjet.services.utils.AlertDialogUtils
import com.example.chatjet.services.utils.ToastUtils
import com.example.chatjet.view_model.MainViewModel
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import kotlinx.android.synthetic.main.fragment_profile_edit.statusColor
import kotlinx.android.synthetic.main.fragment_profile_edit.statusText
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream

class ProfileEditFragment : BaseFragment() {
    override val layout: Int = R.layout.fragment_profile_edit

    private val PROFILE_DEBUG = "TAKE PHOTO"
    private val REQUEST_IMAGE_CAPTURE = 11
    private val REQUEST_IMAGE_PICK = 22

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun subscribeUi() {

        FirebaseRepository().fetchFriends(FirebaseRepository().currentUserUid) { user ->

            if (user.firstLogin == true) {

                AlertDialogUtils.customAlertDialog(
                    requireContext(),
                    "Welcome to ChatJet :)",
                    "You have to complete your profile!"
                ) { mainViewModel.firstLogin() }
            }

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
            changeUserPhoneNumber()
        }

        returnButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileEditFragment_to_profileFragment)
        }

        setupTakePictureClick()
    }

    private fun setupTakePictureClick() {
        photoProfileButton.setOnClickListener {
            val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery")
            val icons = arrayOf(R.drawable.ic_baseline_camera_24, R.drawable.ic_baseline_image_24)
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Choose your profile picture:")

            builder.setItems(options) { dialog, item ->
                when {
                    options[item] == "Take Photo" -> {
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE)
                        try {
                            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
                        } catch (exc: Exception) {
                            Log.d(PROFILE_DEBUG, exc.message.toString())
                        }
                    }
                    options[item] == "Choose from Gallery" -> {
                        val intent =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(intent, REQUEST_IMAGE_PICK)
                    }
                }
            }

            val alertDialog = builder.create()
            val window = alertDialog.window
            val layoutParams = window?.attributes
            layoutParams?.gravity = Gravity.BOTTOM
            window?.attributes = layoutParams
            alertDialog.show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_IMAGE_CAPTURE -> {
                if (resultCode == Activity.RESULT_OK) {
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
            REQUEST_IMAGE_PICK -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val selectedImage = data.data
                    selectedImage?.let {
                        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                        val cursor = requireActivity().contentResolver.query(
                            it,
                            filePathColumn,
                            null,
                            null,
                            null
                        )
                        cursor?.moveToFirst()
                        val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
                        val imagePath = cursor?.getString(columnIndex!!)
                        cursor?.close()

                        Glide.with(this)
                            .load(imagePath)
                            .circleCrop()
                            .override(450, 450)
                            .into(photoProfileButton)

                        val file = File(imagePath ?: "")
                        val fileInputStream = FileInputStream(file)
                        val byteArray = fileInputStream.readBytes()

                        mainViewModel.uploadUserPhoto(byteArray)
                    }
                }
            }
            else -> {
                Log.d(PROFILE_DEBUG, "Unknown request code: $requestCode")
            }
        }
    }

    private fun changeUserPhoneNumber() {
        // We check that user enters 9 digits of phone number or the field is empty
        if (phoneNumberET.text.isEmpty() || phoneNumberET.text.matches(Regex("^\\d{9}$"))) {
            val name = fullNameET.text.toString()
            val number = phoneNumberET.text.toString()
            val location = locationET.text.toString()

            val status = statusText.text != "Not active"

            mainViewModel.updateDataOfUser(name, number, location, status, findNavController())

            ToastUtils.showToast(
                "Success!",
                R.drawable.ic_baseline_check_circle_outline_24,
                R.color.green,
                Toast.LENGTH_SHORT
            )

            fullNameET.setText("")
            phoneNumberET.setText("")
            locationET.setText("")

        } else {

            ToastUtils.showToast(
                "Your phone number should have 9 digits!",
                R.drawable.ic_baseline_remove_circle_outline_24,
                R.color.red,
                Toast.LENGTH_SHORT
            )

        }
    }

    override fun unsubscribeUi() {

    }
}
