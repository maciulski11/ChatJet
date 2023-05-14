package com.example.chatjet.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.chatjet.R
import com.example.chatjet.services.repository.FirebaseRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private var backPressedListener: OnBackPressedListener? = null
    private val db = FirebaseFirestore.getInstance()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val navController = findNavController(R.id.fragment)

        bottomNavigationView.selectedItemId = R.id.messageFragment

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.messageFragment -> {
                    backPressedListener = null
                    navController.navigate(R.id.messageFragment)
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

        if (navController.currentDestination?.id == R.id.messageFragment) {
            menuInflater.inflate(R.menu.invitation_menu, menu)
            return true
        }
        return false
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val menuItem = menu.findItem(R.id.invitation)

        if (FirebaseRepository().currentUserUid.isNotEmpty()) {
            // Pobierz informacje z Firestore dotyczące powiadomień
            db.collection(FirebaseRepository.USERS)
                .document(FirebaseRepository().currentUserUid)
                .collection(FirebaseRepository.INVITATIONS_RECEIVED)
                .whereEqualTo("status", "new")
                .get()
                .addOnSuccessListener { queryDocumentSnapshots ->
                    val hasNewNotifications = queryDocumentSnapshots.isEmpty.not()

                    // Zaktualizuj ikonę na podstawie informacji
                    if (menuItem != null) {
                        if (hasNewNotifications) {
                            menuItem.setIcon(R.drawable.new_notification)
                        } else {
                            menuItem.setIcon(R.drawable.notification)
                        }
                    }
                }
        }

        return super.onPrepareOptionsMenu(menu)
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
