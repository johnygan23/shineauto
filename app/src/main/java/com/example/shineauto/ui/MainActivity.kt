package com.example.shineauto.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.shineauto.R
import com.example.shineauto.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    // Make binding private to prevent external access
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Fix for enableEdgeToEdge: Prevent layout from hiding behind system bars
        ViewCompat.setOnApplyWindowInsetsListener(binding.drawerLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.materialToolbar)
        binding.navView.setNavigationItemSelectedListener(this)

        // 2. Setup Drawer Toggle
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.materialToolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // 3. Modern Back Press Handling (Replaces onBackPressed)
        onBackPressedDispatcher.addCallback(this) {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                // Default back behavior
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
                isEnabled = true
            }
        }

        // Initial Fragment Load
        if (savedInstanceState == null) {
            navigateToFragment(CustomerServiceListFragment())
            binding.navView.setCheckedItem(R.id.nav_services)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // 4. Fixed Resource IDs (Removed _root_ide_package_)
        when (item.itemId) {
            R.id.nav_services -> navigateToFragment(CustomerServiceListFragment())
            R.id.nav_appointments -> navigateToFragment(CustomerBookingsFragment())
            R.id.nav_about_us -> navigateToFragment(InformationFragment())
            R.id.nav_profile -> navigateToFragment(ProfileFragment())
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()

        // Update the visual selection in the drawer
        val checkedId = when (fragment) {
            is CustomerServiceListFragment -> R.id.nav_services
            is CustomerBookingsFragment -> R.id.nav_appointments
            is InformationFragment -> R.id.nav_about_us
            is ProfileFragment -> R.id.nav_profile
            else -> null
        }

        if (checkedId != null) {
            binding.navView.setCheckedItem(checkedId)
        }
    }

    fun logout() {
        // 1. Clear the "Session" data
        val sharedPreferences = getSharedPreferences("ShineAutoPrefs", MODE_PRIVATE)
        sharedPreferences.edit {
            clear()
        }

        // 2. Navigate back to Login Screen
        val intent = Intent(this, LoginActivity::class.java)
        // 3. Clear the back stack so the user can't press "Back" to return
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}