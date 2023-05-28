package com.example.chatjet.services.utils

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.TextView
import com.example.chatjet.R

object AlertDialogUtils {

    fun customAlertDialog(context: Context, titleText: String, infoText: String, success: () -> Unit) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.custom_dialog, null)
        val alertDialog = Dialog(context)
        alertDialog.setContentView(dialogView)
        alertDialog.findViewById<TextView>(R.id.infoDialogTV).text = infoText
        alertDialog.findViewById<TextView>(R.id.titleDialogTV).text = titleText

        // Dostosuj rozmiar dialogu
        val window = alertDialog.window
        val layoutParams = window?.attributes
        layoutParams?.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.attributes = layoutParams

        // Zablokuj możliwość kliknięcia poza dialogiem
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()

        dialogView.findViewById<TextView>(R.id.dialogButton).setOnClickListener {
            success()
            alertDialog.dismiss()
        }
    }
}