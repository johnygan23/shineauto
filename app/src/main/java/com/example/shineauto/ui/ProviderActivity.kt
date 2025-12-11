package com.example.shineauto.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.shineauto.R
import com.example.shineauto.databinding.ActivityProviderBinding
import com.google.android.material.navigation.NavigationView

class ProviderActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityProviderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.materialToolbar)

        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.materialToolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)

        if (savedInstanceState == null) {
            loadFragment(ProviderOrdersFragment())
            binding.navView.setCheckedItem(R.id.nav_provider_orders)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_provider_orders -> loadFragment(ProviderOrdersFragment())
            R.id.nav_provider_services -> loadFragment(ProviderServicesFragment())
            R.id.nav_provider_accepted_orders -> loadFragment(ProviderAcceptedOrdersFragment())
            R.id.nav_provider_profile -> loadFragment(ProfileFragment())
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
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