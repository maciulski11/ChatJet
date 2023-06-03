package com.example.chatjet.services.utils

import androidx.navigation.NavOptions
import com.example.chatjet.R

object AnimationUtils {

    // Define navigation options for the notification animation
    val downNavAnim = NavOptions.Builder()
        .setEnterAnim(R.anim.slide_in_up)          // Animation for entering: sliding up from the bottom
        .setExitAnim(R.anim.slide_out_bottom)      // Animation for exiting: sliding down from the top
        .setPopEnterAnim(R.anim.slide_in_bottom)   // Animation for re-entering: sliding up from the bottom
        .setPopExitAnim(R.anim.slide_out_up)       // Animation for re-exiting: sliding down from the top
        .build()

    val topNavAnim = NavOptions.Builder()
        .setEnterAnim(R.anim.slide_in_bottom)
        .setExitAnim(R.anim.slide_out_up)
        .setPopEnterAnim(R.anim.slide_in_up)
        .setPopExitAnim(R.anim.slide_out_bottom)
        .build()

    val rightNavAnim = NavOptions.Builder()
        .setEnterAnim(R.anim.slide_in_right)        // Animation for entering: sliding up from the left
        .setExitAnim(R.anim.slide_out_left)         // Animation for entering: sliding up from the right
        .setPopEnterAnim(R.anim.slide_in_left)      // Animation for re-entering: sliding up from the left
        .setPopExitAnim(R.anim.slide_out_right)     // Animation for re-entering: sliding up from the right
        .build()

    val leftNavAnim = NavOptions.Builder()
        .setEnterAnim(R.anim.slide_in_left)
        .setExitAnim(R.anim.slide_out_right)
        .setPopEnterAnim(R.anim.slide_in_right)
        .setPopExitAnim(R.anim.slide_out_left)
        .build()
}