package com.example.shineauto.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.shineauto.data.ShineAutoDatabase
import com.example.shineauto.databinding.FragmentAdminDashboardBinding
import kotlinx.coroutines.launch

class AdminDashboardFragment : Fragment() {

    private var _binding: FragmentAdminDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdminDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadStats()
    }

    private fun loadStats() {
        lifecycleScope.launch {
            val db = ShineAutoDatabase.getDatabase(requireContext())
            val userCount = db.userDao().getUserCount()
            val serviceCount = db.serviceDao().getServiceCount()
            val bookingCount = db.bookingDao().getBookingCount()

            binding.textTotalUsers.text = userCount.toString()
            binding.textTotalServices.text = serviceCount.toString()
            binding.textTotalBookings.text = bookingCount.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}