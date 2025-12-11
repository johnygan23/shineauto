package com.example.shineauto.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shineauto.data.ShineAutoDatabase
import com.example.shineauto.databinding.FragmentCustomerUpcomingBookingsBinding
import com.example.shineauto.ui.adapter.BookingAdapter
import kotlinx.coroutines.launch

// Rename XML to fragment_customer_upcoming_bookings.xml or keep fragment_upcoming_appointments.xml
class CustomerUpcomingBookingsFragment : Fragment() {

    private var _binding: FragmentCustomerUpcomingBookingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: BookingAdapter
    private var currentCustomerId: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCustomerUpcomingBookingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireActivity().getSharedPreferences("ShineAutoPrefs", AppCompatActivity.MODE_PRIVATE)
        currentCustomerId = prefs.getInt("USER_ID", -1)

        adapter = BookingAdapter()
        // Assuming ID is upcomingRecyclerView in your XML
        binding.upcomingRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.upcomingRecyclerView.adapter = adapter

        // Watch the Database for changes
        lifecycleScope.launch {
            ShineAutoDatabase.getDatabase(requireContext())
                .bookingDao()
                .getUpcomingBookings(currentCustomerId)
                .collect { bookings ->
                    // Filter for active bookings if you want (e.g., status != "COMPLETED")
                    // For now, let's show all
                    adapter.submitList(bookings)
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}