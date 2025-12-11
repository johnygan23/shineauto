package com.example.shineauto.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shineauto.R
import com.example.shineauto.databinding.ItemAdminServiceBinding
import com.example.shineauto.model.ServiceItem
import com.example.shineauto.model.User

class AdminServiceAdapter(
    private val onDeleteClick: (ServiceItem) -> Unit
) : RecyclerView.Adapter<AdminServiceAdapter.ViewHolder>() {

    private var services = listOf<ServiceItem>()
    private var allUsers = listOf<User>() // Holds users for lookup

    fun submitList(newServices: List<ServiceItem>) {
        services = newServices
        notifyDataSetChanged()
    }

    // Call this from the Fragment to pass the user list
    fun setAllUsers(users: List<User>) {
        allUsers = users
        notifyDataSetChanged() // Refresh to update names if users load after services
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAdminServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(services[position])
    }

    override fun getItemCount() = services.size

    inner class ViewHolder(private val binding: ItemAdminServiceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(service: ServiceItem) {
            binding.serviceName.text = service.name
            binding.servicePrice.text = "$${service.price}"

            val provider = allUsers.find { it.id == service.providerId }
            if (provider != null) {
                binding.serviceProviderName.text = "Provider: ${provider.username}"
            } else {
                // Debugging help: Shows the ID if name not found
                binding.serviceProviderName.text = "Provider: Unknown (ID: ${service.providerId})"
            }

            // Display Service Image
            if (service.imageUri != null) {
                try {
                    binding.serviceImage.setImageURI(Uri.parse(service.imageUri))
                } catch (e: Exception) {
                    binding.serviceImage.setImageResource(R.drawable.service)
                }
            } else {
                binding.serviceImage.setImageResource(R.drawable.service)
            }

            binding.btnDeleteService.setOnClickListener { onDeleteClick(service) }
        }
    }
}