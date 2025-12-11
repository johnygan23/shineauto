package com.example.shineauto.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shineauto.R
import com.example.shineauto.databinding.ItemCarWashServiceBinding
import com.example.shineauto.model.ServiceItem

class CustomerServiceAdapter(
    private val onServiceClick: (ServiceItem) -> Unit
) : RecyclerView.Adapter<com.example.shineauto.ui.adapter.CustomerServiceAdapter.ServiceViewHolder>() {

    private var services = listOf<ServiceItem>()

    fun submitList(newServices: List<ServiceItem>) {
        services = newServices
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        // Make sure 'item_car_wash_service.xml' matches your layout file name
        val binding = ItemCarWashServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ServiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.bind(services[position])
    }

    override fun getItemCount() = services.size

    inner class ServiceViewHolder(private val binding: ItemCarWashServiceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(service: ServiceItem) {
            binding.serviceTitle.text = service.name

            if (service.imageUri != null) {
                // Load the image from the saved internal storage path (Content Provider URI)
                binding.serviceImage.setImageURI(Uri.parse(service.imageUri))
            } else {
                // Fallback to the default image resource
                binding.serviceImage.setImageResource(R.drawable.standard_wash)
            }
            binding.root.setOnClickListener {
                onServiceClick(service)
            }
        }
    }
}