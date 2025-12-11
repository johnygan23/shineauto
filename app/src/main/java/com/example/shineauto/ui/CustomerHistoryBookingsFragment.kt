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
import com.example.shineauto.databinding.FragmentCustomerHistoryBookingsBinding
import com.example.shineauto.utils.AppUtils
import kotlinx.coroutines.launch

class CustomerHistoryBookingsFragment : Fragment() {

    private var _binding: FragmentCustomerHistoryBookingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: BookingAdapter // Reusing the simple BookingAdapter
    private var currentCustomerId: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Assuming your layout is named fragment_customer_history_bookings.xml
        _binding = FragmentCustomerHistoryBookingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireActivity().getSharedPreferences("ShineAutoPrefs", AppCompatActivity.MODE_PRIVATE)
        currentCustomerId = prefs.getInt("USER_ID", -1)

        adapter = BookingAdapter()
        // Assuming ID is historyRecyclerView in your XML
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.historyRecyclerView.adapter = adapter

        loadHistoryBookings()
    }

    private fun loadHistoryBookings() {
        lifecycleScope.launch {
            ShineAutoDatabase.getDatabase(requireContext())
                .bookingDao()
                .getHistoryBookings(currentCustomerId) // Fetches COMPLETED or CANCELLED
                .collect { bookings ->

                    if (bookings.isEmpty()) {
                        binding.emptyStateText.visibility = View.VISIBLE
                        binding.historyRecyclerView.visibility = View.GONE
                    } else {
                        binding.emptyStateText.visibility = View.GONE
                        binding.historyRecyclerView.visibility = View.VISIBLE
                        adapter.submitList(bookings)
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}