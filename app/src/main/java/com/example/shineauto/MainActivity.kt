package com.example.shineauto

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.shineauto.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.materialToolbar)

        binding.navView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.materialToolbar, 0, 0
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            navigateToFragment(Fragment1())
            binding.navView.setCheckedItem(R.id.nav_services)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_services -> navigateToFragment(Fragment1())
            R.id.nav_appointments -> navigateToFragment(Fragment2())
            R.id.nav_about_us -> navigateToFragment(Fragment3())
            R.id.nav_profile -> navigateToFragment(Fragment4())
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()

        // Update the checked item in the navigation drawer
        when (fragment) {
            is Fragment1 -> binding.navView.setCheckedItem(R.id.nav_services)
            is Fragment2 -> binding.navView.setCheckedItem(R.id.nav_appointments)
            is Fragment3 -> binding.navView.setCheckedItem(R.id.nav_about_us)
            is Fragment4 -> binding.navView.setCheckedItem(R.id.nav_profile)
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
