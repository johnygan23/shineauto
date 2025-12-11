package com.example.shineauto.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shineauto.databinding.ItemAppointmentBinding
import com.example.shineauto.model.Booking

class BookingAdapter : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {
    private var items = listOf<Booking>()

    fun submitList(newItems: List<Booking>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = ItemAppointmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class BookingViewHolder(private val binding: ItemAppointmentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(booking: Booking) {
            binding.serviceNameText.text = booking.serviceName
            binding.appointmentDateText.text = "Date: ${booking.date}"
            binding.appointmentTimeText.text = "Time: ${booking.time}"
            binding.appointmentAddressText.text = "Status: ${booking.status}"
            // Reuse address text field for Status for now, or add a status TextView
        }
    }
}