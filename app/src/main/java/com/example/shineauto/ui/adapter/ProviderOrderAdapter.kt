package com.example.shineauto.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shineauto.databinding.ItemProviderOrderRequestBinding
import com.example.shineauto.model.Booking

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

            binding.textCustomerInfo.text = "Customer: ${booking.customerName}"
            binding.textAddress.text = "Address: ${booking.address}"

            binding.textDate.text = booking.date
            binding.textTime.text = booking.time

            binding.btnAccept.setOnClickListener { onAccept(booking) }
            binding.btnReject.setOnClickListener { onReject(booking) }
        }
    }
}