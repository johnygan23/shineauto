package com.example.shineauto

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.shineauto.ui.HistoryAppointmentsFragment
import com.example.shineauto.ui.UpcomingAppointmentsFragment

class AppointmentsPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> UpcomingAppointmentsFragment()
            1 -> HistoryAppointmentsFragment()
            else -> throw IllegalStateException("Invalid position: $position")
        }
    }
}
