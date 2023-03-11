package com.example.chatjet.services.s.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.chatjet.R
import com.example.chatjet.ui.activity.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

private const val CHANNEL_ID = "my_notification_channel"

class FirebaseServices : FirebaseMessagingService() {


    

//    companion object{
//        var sharedPref: SharedPreferences? = null
//
//        var token:String?
//            get(){
//                return sharedPref?.getString("token","")
//            }
//            set(value){
//                sharedPref?.edit()?.putString("token",value)?.apply()
//            }
//    }
//
//    override fun onNewToken(newToken: String) {
//        super.onNewToken(newToken)
//        token = newToken
//    }
//
//    @RequiresApi(Build.VERSION_CODES.S)
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        super.onMessageReceived(remoteMessage)
//
//        Log.d("REPO_NOTIFICATION", "onMessageRecived kork 3")
//
//
//        val intent = Intent(this, MainActivity::class.java)
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        val notificationId = Random.nextInt()
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            createNotificationChannel(notificationManager)
//        }
//
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        val pendingIntent = PendingIntent.getActivity(this,0,  intent, FLAG_MUTABLE)
//        val notification = NotificationCompat.Builder(this,CHANNEL_ID)
//            .setContentTitle(remoteMessage.data["title"])
//            .setContentText(remoteMessage.data["message"])
//            .setSmallIcon(R.drawable.ic_baseline_chat_24)
//            .setAutoCancel(true)
//            .setContentIntent(pendingIntent)
//            .build()
//
//        notificationManager.notify(notificationId,notification)
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun createNotificationChannel(notificationManager: NotificationManager){
//
//        Log.d("REPO_NOTIFICATION", "createnotificationchannel kork 4")
//
//        val channelName = "ChannelFirebaseChat"
//        val channel = NotificationChannel(CHANNEL_ID,channelName,IMPORTANCE_HIGH).apply {
//            description="MY FIREBASE CHAT DESCRIPTION"
//            enableLights(true)
//            lightColor = Color.BLACK
//        }
//        notificationManager.createNotificationChannel(channel)
//
//    }

}