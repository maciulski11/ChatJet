package com.example.chatjet.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import com.example.chatjet.R
import com.example.chatjet.services.repository.FirebaseRepository
import com.example.chatjet.services.utils.AnimationUtils
import com.example.chatjet.services.utils.ToastUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private var backPressedListener: OnBackPressedListener? = null
    private val db = FirebaseFirestore.getInstance()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.AppTheme_Light)

        setContentView(R.layout.activity_main)

        // Set light/dark mode depending on system settings
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        ToastUtils.initialize(this)

        bottomNavView()
    }

    private fun bottomNavView() {
        val navController = findNavController(R.id.fragment)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        bottomNavigationView.selectedItemId = R.id.messageFragment

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            // Check if the button is already selected
            val isButtonSelected = when (menuItem.itemId) {
                R.id.messageFragment -> navController.currentDestination?.id != R.id.messageFragment
                R.id.profileFragment -> navController.currentDestination?.id != R.id.profileFragment
                R.id.findUserFragment -> navController.currentDestination?.id != R.id.findUserFragment
                else -> false
            }

            // Navigate to the selected fragment if the button is not already selected
            if (isButtonSelected) {
                when (menuItem.itemId) {
                    R.id.messageFragment -> {
                        backPressedListener = null
                        navController.navigate(
                            R.id.messageFragment,
                            null,
                            AnimationUtils.topNavAnim
                        )
                    }
                    R.id.profileFragment -> {
                        backPressedListener = null
                        navController.navigate(
                            R.id.profileFragment,
                            null,
                            AnimationUtils.rightNavAnim
                        )
                    }
                    R.id.findUserFragment -> {
                        backPressedListener = null
                        navController.navigate(
                            R.id.findUserFragment,
                            null,
                            AnimationUtils.leftNavAnim
                        )
                    }
                }
            }
            isButtonSelected
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
        // Get the NavController
        val navController = findNavController(R.id.fragment)

        // Inflate the menu for the invitation fragment if it is the current destination
        if (navController.currentDestination?.id in listOf(
                R.id.messageFragment,
                R.id.profileFragment,
                R.id.findUserFragment
            )
        ) {
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
//                .whereEqualTo("status", "new") // off new notification icon
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
        val navController = findNavController(R.id.fragment)

        // Handle the click event for the invitation menu item
        when (item.itemId) {
            R.id.invitation -> {
                // Navigate to the invitation fragment only if it is not the current destination
                if (navController.currentDestination?.id != R.id.invitationFragment) {
                    navController.navigate(
                        R.id.invitationFragment,
                        null,
                        AnimationUtils.downNavAnim
                    )
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

interface OnBackPressedListener {
    fun onBackPressed(): Boolean
}
