package com.example.shineauto

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.shineauto.ui.CustomerHistoryBookingsFragment
import com.example.shineauto.ui.CustomerUpcomingBookingsFragment

class AppointmentsPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CustomerUpcomingBookingsFragment()
            1 -> CustomerHistoryBookingsFragment()
            else -> throw IllegalStateException("Invalid position: $position")
        }
    }
}
