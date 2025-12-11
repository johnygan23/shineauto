package com.example.shineauto.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shineauto.data.ShineAutoDatabase
import com.example.shineauto.databinding.FragmentProviderOrdersBinding
import com.example.shineauto.databinding.ItemProviderOrderRequestBinding
import com.example.shineauto.model.Booking
import kotlinx.coroutines.launch

class ProviderOrdersFragment : Fragment() {

    private var _binding: FragmentProviderOrdersBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ProviderOrderAdapter
    private var currentProviderId: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProviderOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Get Provider ID
        val prefs = requireActivity().getSharedPreferences("ShineAutoPrefs", AppCompatActivity.MODE_PRIVATE)
        currentProviderId = prefs.getInt("USER_ID", -1)

        // 2. Setup RecyclerView
        adapter = ProviderOrderAdapter(
            onAccept = { booking -> updateBookingStatus(booking, "ACCEPTED") },
            onReject = { booking -> updateBookingStatus(booking, "CANCELLED") }
        )
        binding.ordersRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.ordersRecyclerView.adapter = adapter

        // 3. Observe Live Data (Flow) from Room
        lifecycleScope.launch {
            ShineAutoDatabase.getDatabase(requireContext())
                .bookingDao()
                .getPendingRequests(currentProviderId)
                .collect { bookingList ->
                    if (bookingList.isEmpty()) {
                        binding.emptyStateText.visibility = View.VISIBLE
                        binding.ordersRecyclerView.visibility = View.GONE
                    } else {
                        binding.emptyStateText.visibility = View.GONE
                        binding.ordersRecyclerView.visibility = View.VISIBLE
                        adapter.submitList(bookingList)
                    }
                }
        }
    }

    private fun updateBookingStatus(booking: Booking, newStatus: String) {
        lifecycleScope.launch {
            val updatedBooking = booking.copy(status = newStatus)
            ShineAutoDatabase.getDatabase(requireContext())
                .bookingDao()
                .updateBooking(updatedBooking)

            val message = if (newStatus == "ACCEPTED") "Order Accepted!" else "Order Rejected"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// --- ADAPTER ---
class ProviderOrderAdapter(
    private val onAccept: (Booking) -> Unit,
    private val onReject: (Booking) -> Unit
) : RecyclerView.Adapter<ProviderOrderAdapter.ViewHolder>() {

    private var items = listOf<Booking>()

    fun submitList(newItems: List<Booking>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProviderOrderRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(private val binding: ItemProviderOrderRequestBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(booking: Booking) {
            binding.textServiceName.text = booking.serviceName
            binding.textCustomerInfo.text = "Customer ID: #${booking.customerId}"
            binding.textDate.text = booking.date
            binding.textTime.text = booking.time

            binding.btnAccept.setOnClickListener { onAccept(booking) }
            binding.btnReject.setOnClickListener { onReject(booking) }
        }
    }
}