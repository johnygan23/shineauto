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
import com.example.shineauto.databinding.DialogServiceEditorBinding
import com.example.shineauto.databinding.FragmentProviderServicesBinding
import com.example.shineauto.databinding.ItemProviderServiceBinding
import com.example.shineauto.model.ServiceItem
import com.example.shineauto.utils.AppUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch

class ProviderServicesFragment : Fragment() {

    private var _binding: FragmentProviderServicesBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ProviderServiceAdapter
    private var currentProviderId: Int = 0

    // 1. Variable to temporarily hold the URI of the selected image
    private var selectedImagePath: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProviderServicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Get Logged in Provider ID
        val prefs = requireActivity().getSharedPreferences("ShineAutoPrefs", AppCompatActivity.MODE_PRIVATE)
        currentProviderId = prefs.getInt("USER_ID", -1)

        // 2. Setup RecyclerView
        adapter = ProviderServiceAdapter(
            onEditClick = { service -> showAddEditDialog(service) },
            onDeleteClick = { service -> deleteService(service) }
        )
        binding.servicesRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.servicesRecyclerView.adapter = adapter

        // 3. Load Data
        loadServices()

        // 4. Add Button
        binding.fabAddService.setOnClickListener {
            showAddEditDialog(null) // null means "New Service"
        }
    }

    private fun loadServices() {
        lifecycleScope.launch {
            val services = ShineAutoDatabase.getDatabase(requireContext())
                .serviceDao()
                .getServicesByProvider(currentProviderId)
            adapter.submitList(services)
        }
    }

    private fun deleteService(service: ServiceItem) {
        lifecycleScope.launch {
            ShineAutoDatabase.getDatabase(requireContext()).serviceDao().deleteService(service)
            Toast.makeText(context, "Service Deleted", Toast.LENGTH_SHORT).show()
            loadServices() // Refresh list
        }
    }

    private fun showAddEditDialog(serviceToEdit: ServiceItem?) {
        val dialog = BottomSheetDialog(requireContext())
        val sheetBinding = DialogServiceEditorBinding.inflate(layoutInflater)
        dialog.setContentView(sheetBinding.root)

        // If Editing, pre-fill data
        if (serviceToEdit != null) {
            sheetBinding.editServiceName.setText(serviceToEdit.name)
            sheetBinding.editServicePrice.setText(serviceToEdit.price.toString())
            sheetBinding.editServiceDesc.setText(serviceToEdit.description)
            sheetBinding.editServiceRegion.setText(serviceToEdit.region)
            sheetBinding.btnSaveService.text = "Update Service"
        }

        sheetBinding.btnSaveService.setOnClickListener {
            val name = sheetBinding.editServiceName.text.toString()
            val price = sheetBinding.editServicePrice.text.toString().toDoubleOrNull() ?: 0.0
            val desc = sheetBinding.editServiceDesc.text.toString()
            val region = sheetBinding.editServiceRegion.text.toString()

            if (name.isNotEmpty() && region.isNotEmpty()) {
                lifecycleScope.launch {
                    val dao = ShineAutoDatabase.getDatabase(requireContext()).serviceDao()

                    if (serviceToEdit == null) {
                        // Create New
                        val newService = ServiceItem(
                            providerId = currentProviderId,
                            name = name,
                            price = price,
                            description = desc,
                            region = region,
                            imageUri = selectedImagePath
                        )
                        dao.addService(newService)
                    } else {
                        // Update Existing
                        val updatedService = serviceToEdit.copy(
                            name = name,
                            price = price,
                            description = desc,
                            region = region
                        )
                        dao.updateService(updatedService)
                    }
                    dialog.dismiss()
                    loadServices() // Refresh List
                }
            } else {
                Toast.makeText(context, "Please fill required fields", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// --- ADAPTER CLASS ---
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