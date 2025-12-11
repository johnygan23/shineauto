package com.example.shineauto.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shineauto.databinding.ItemProviderAcceptedOrderBinding
import com.example.shineauto.model.Booking
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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