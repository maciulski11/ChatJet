package com.example.chatjet.services.utils

import android.content.Context
import android.graphics.PorterDuff
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.chatjet.R

object ToastUtils {

    private var appContext: Context? = null

    // Initialized it in activity and I don't have to use context in every Fragments
    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    fun showToast(
        message: String,
        backgroundToastColor: Int,
        iconResId: Int,
        duration: Int
    ) {
        appContext?.let {
            customToast(
                message,
                iconResId,
                R.color.white,
                backgroundToastColor,
                duration
            )
        }
    }

    //TODO: blad z kolorem backgroundToastColor
    private fun customToast(
        message: String,
        iconResId: Int,
        textColor: Int,
        backgroundToastColor: Int,
        duration: Int
    ) {
        val toast = Toast.makeText(appContext, message, Toast.LENGTH_LONG)
        // Creates a LayoutInflater object which can be used to inflate a custom layout.
        val inflater =
            appContext?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        // Inflates the custom layout for the toast, which is defined in the R.layout.custom_toast XML file.
        val view = inflater.inflate(R.layout.custom_toast, null)
        val backgroundDrawable =
            ContextCompat.getDrawable(appContext!!, R.drawable.custom_toast_background)
        backgroundDrawable?.setColorFilter(
            ContextCompat.getColor(
                appContext!!,
                backgroundToastColor
            ), PorterDuff.Mode.SRC_IN
        )
        view.background = backgroundDrawable
        val textView = view.findViewById<TextView>(R.id.toast_text)
        // Gets the Drawable resource for the icon from the app's resources
        val icon = ContextCompat.getDrawable(appContext!!, iconResId)
        // This code retrieves the ImageView with id toast_icon from the custom toast layout,
        // sets the tint color of the provided icon to white, and sets it as the image for the ImageView.
        val iconView = view.findViewById<ImageView>(R.id.toast_icon)
        icon?.let {
            DrawableCompat.setTint(it, ContextCompat.getColor(appContext!!, textColor))
            iconView.setImageDrawable(it)
        }
        view.background = backgroundDrawable
        textView.text = message
        textView.gravity = Gravity.CENTER
        textView.setTextColor(ContextCompat.getColor(appContext!!, textColor))

        toast.duration = duration
        toast.view = view
        toast.show()
    }
}
