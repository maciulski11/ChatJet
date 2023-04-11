package com.example.chatjet.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.chatjet.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity: AppCompatActivity() {

    private var backPressedListener: OnBackPressedListener? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val navController = findNavController(R.id.fragment)

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.friendsFragment -> {
                    backPressedListener = null
                    navController.navigate(R.id.friendsFragment)
                    true
                }
                R.id.profileFragment -> {
                    backPressedListener = null
                    navController.navigate(R.id.profileFragment)
                    true
                }
                R.id.findUserFragment -> {
                    backPressedListener = null
                    navController.navigate(R.id.findUserFragment)
                    true
                }
                // itd. dla pozostałych pozycji w menu
                else -> false
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (backPressedListener != null && backPressedListener!!.onBackPressed()) {
            return
        }
        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val navController = findNavController(R.id.fragment)

        if (navController.currentDestination?.id == R.id.friendsFragment) {
            menuInflater.inflate(R.menu.invitation_menu, menu)
            return true
        }
        return false
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.invitation -> {
                findNavController(R.id.fragment).navigate(R.id.invitationFragment)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

interface OnBackPressedListener {
    fun onBackPressed(): Boolean
}
