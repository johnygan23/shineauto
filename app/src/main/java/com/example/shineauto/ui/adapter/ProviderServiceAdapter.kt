package com.example.shineauto.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shineauto.databinding.ItemProviderServiceBinding
import com.example.shineauto.model.ServiceItem

class ProviderServiceAdapter(
    private val onEditClick: (ServiceItem) -> Unit,
    private val onDeleteClick: (ServiceItem) -> Unit
) : RecyclerView.Adapter<ProviderServiceAdapter.ViewHolder>() {

    private var items = listOf<ServiceItem>()

    fun submitList(newItems: List<ServiceItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProviderServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(private val binding: ItemProviderServiceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ServiceItem) {
            binding.serviceName.text = item.name
            binding.servicePrice.text = "$${item.price}"
            binding.serviceDescription.text = item.description

            binding.btnEdit.setOnClickListener { onEditClick(item) }
            binding.btnDelete.setOnClickListener { onDeleteClick(item) }
        }
    }
}