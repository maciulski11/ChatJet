package com.example.chatjet.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.chatjet.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity: AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
//        bottomNavigationView.setBackgroundColor(Color.BLACK)
        val navController = findNavController(R.id.fragment)

        bottomNavigationView.setupWithNavController(navController)
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
        return false
    }
}
