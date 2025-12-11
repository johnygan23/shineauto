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
import androidx.recyclerview.widget.RecyclerView
import com.example.shineauto.data.ShineAutoDatabase
import com.example.shineauto.databinding.FragmentProviderAcceptedOrdersBinding
import com.example.shineauto.databinding.ItemProviderAcceptedOrderBinding
import com.example.shineauto.model.Booking
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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

// --- ADAPTER ---
class AcceptedOrderAdapter(
    private val onMarkCompletedClick: (Booking) -> Unit
) : RecyclerView.Adapter<AcceptedOrderAdapter.ViewHolder>() {

    private var items = listOf<Booking>()

    fun submitList(newItems: List<Booking>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProviderAcceptedOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(private val binding: ItemProviderAcceptedOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Booking) {
            binding.textServiceName.text = "${item.serviceName}"
            binding.textCustomerInfo.text = "Customer: ${item.customerName}"
            binding.textAddress.text = "Address: ${item.address}"
            binding.textDateTime.text = "Scheduled: ${item.date} at ${item.time}"

            // --- FIX: Hide button if date is in the future ---
            if (isDateInFuture(item.date)) {
                binding.btnMarkCompleted.visibility = View.GONE
                binding.btnMarkCompleted.isEnabled = false
            } else {
                binding.btnMarkCompleted.visibility = View.VISIBLE
                binding.btnMarkCompleted.isEnabled = true
                binding.btnMarkCompleted.setOnClickListener { onMarkCompletedClick(item) }
            }
        }

        private fun isDateInFuture(dateString: String): Boolean {
            val sdf = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
            return try {
                val bookingDate = sdf.parse(dateString) ?: return false
                val today = Date()

                // Reset hours/minutes/seconds for accurate day comparison
                val bookingCal = Calendar.getInstance().apply { time = bookingDate }
                val todayCal = Calendar.getInstance().apply { time = today }

                // Clear time parts
                bookingCal.set(Calendar.HOUR_OF_DAY, 0)
                bookingCal.set(Calendar.MINUTE, 0)
                bookingCal.set(Calendar.SECOND, 0)
                bookingCal.set(Calendar.MILLISECOND, 0)

                todayCal.set(Calendar.HOUR_OF_DAY, 0)
                todayCal.set(Calendar.MINUTE, 0)
                todayCal.set(Calendar.SECOND, 0)
                todayCal.set(Calendar.MILLISECOND, 0)

                // Return true if Booking Date is AFTER Today
                bookingCal.after(todayCal)
            } catch (e: Exception) {
                false // If parsing fails, default to showing the button
            }
        }
    }
}
