package com.example.shineauto.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shineauto.data.ShineAutoDatabase
import com.example.shineauto.databinding.FragmentProviderAcceptedOrdersBinding
import com.example.shineauto.model.Booking
import com.example.shineauto.ui.adapter.AcceptedOrderAdapter
import kotlinx.coroutines.launch

class ProviderAcceptedOrdersFragment : Fragment() {

    private var _binding: FragmentProviderAcceptedOrdersBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AcceptedOrderAdapter
    private var currentProviderId: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProviderAcceptedOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireActivity().getSharedPreferences("ShineAutoPrefs", AppCompatActivity.MODE_PRIVATE)
        currentProviderId = prefs.getInt("USER_ID", -1)

        adapter = AcceptedOrderAdapter { booking ->
            showCompletionDialog(booking)
        }
        binding.acceptedOrdersRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.acceptedOrdersRecyclerView.adapter = adapter

        loadAcceptedOrders()
    }

    private fun loadAcceptedOrders() {
        lifecycleScope.launch {
            ShineAutoDatabase.getDatabase(requireContext())
                .bookingDao()
                .getAcceptedOrders(currentProviderId)
                .collect { bookingList ->
                    if (bookingList.isEmpty()) {
                        binding.emptyStateText.visibility = View.VISIBLE
                        binding.acceptedOrdersRecyclerView.visibility = View.GONE
                    } else {
                        binding.emptyStateText.visibility = View.GONE
                        binding.acceptedOrdersRecyclerView.visibility = View.VISIBLE
                        adapter.submitList(bookingList)
                    }
                }
        }
    }

    private fun showCompletionDialog(booking: Booking) {
        AlertDialog.Builder(requireContext())
            .setTitle("Service Completion")
            .setMessage("Mark service for ${booking.customerName} scheduled on ${booking.date} as completed?")
            .setPositiveButton("Complete") { _, _ ->
                updateBookingStatus(booking, "COMPLETED")
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateBookingStatus(booking: Booking, newStatus: String) {
        lifecycleScope.launch {
            val updatedBooking = booking.copy(status = newStatus)
            ShineAutoDatabase.getDatabase(requireContext()).bookingDao().updateBooking(updatedBooking)
            Toast.makeText(context, "Service marked as COMPLETED!", Toast.LENGTH_SHORT).show()
            // The flow collector in loadAcceptedOrders() will automatically refresh the list
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}