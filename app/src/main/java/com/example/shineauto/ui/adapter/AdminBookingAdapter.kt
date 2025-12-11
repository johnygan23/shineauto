package com.example.shineauto.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shineauto.databinding.ItemAdminBookingBinding
import com.example.shineauto.model.Booking

class AdminBookingAdapter : RecyclerView.Adapter<AdminBookingAdapter.ViewHolder>() {

    private var bookings = listOf<Booking>()

    fun submitList(newBookings: List<Booking>) {
        bookings = newBookings
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAdminBookingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(bookings[position])
    }

    override fun getItemCount() = bookings.size

    inner class ViewHolder(private val binding: ItemAdminBookingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(booking: Booking) {
            binding.bookingService.text = booking.serviceName
            binding.bookingCustomer.text = "Customer: ${booking.customerName}"
            binding.bookingAddress.text = "Address: ${booking.address}"
            binding.bookingDate.text = "${booking.date} at ${booking.time}"
            binding.bookingStatus.text = booking.status

            // Simple Status Coloring
            when (booking.status) {
                "PENDING" -> binding.bookingStatus.setTextColor(Color.parseColor("#FFA500")) // Orange
                "ACCEPTED" -> binding.bookingStatus.setTextColor(Color.BLUE)
                "COMPLETED" -> binding.bookingStatus.setTextColor(Color.parseColor("#008000")) // Green
                "CANCELLED" -> binding.bookingStatus.setTextColor(Color.RED)
            }
        }
    }
}