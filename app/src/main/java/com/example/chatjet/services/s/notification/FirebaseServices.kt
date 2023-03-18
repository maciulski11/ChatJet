package com.example.chatjet.services.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.Context
import android.content.Intent
import android.content.Intent.getIntent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.chatjet.R
import com.example.chatjet.ui.activity.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

private const val CHANNEL_ID = "my_notification_channel"
private const val TAG = "MyFirebaseMessagingService"

class FirebaseServices : FirebaseMessagingService() {

    private val sharedPref: SharedPreferences by lazy {
        getSharedPreferences("tokenPrefs", Context.MODE_PRIVATE)
    }

    private var token: String?
        get() = sharedPref.getString("token", "")
        set(value) = sharedPref.edit().putString("token", value).apply()

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        Log.d(TAG, "Refreshed token: $newToken")
        token = newToken
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "onMessageReceived")

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = Random.nextInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        // Odczytaj tytuł i treść wiadomości
        val title = remoteMessage.data["title"]
        val message = remoteMessage.data["message"]

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_MUTABLE)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_baseline_chat_24)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        Log.d("REPO NOTIFICATION XX", "${remoteMessage.data["message"]}")


        notificationManager.notify(notificationId, notification)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        Log.d(TAG, "createNotificationChannel")

        val channelName = "ChannelFirebaseChat"
        val channel = NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply {
            description = "My Firebase Chat Description"
            enableLights(true)
            lightColor = Color.BLACK
        }
        notificationManager.createNotificationChannel(channel)
    }
}
