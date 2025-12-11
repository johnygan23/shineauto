package com.example.shineauto.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shineauto.data.ShineAutoDatabase
import com.example.shineauto.databinding.DialogServiceEditorBinding
import com.example.shineauto.databinding.FragmentProviderServicesBinding
import com.example.shineauto.model.ServiceItem
import com.example.shineauto.ui.adapter.ProviderServiceAdapter
import com.example.shineauto.utils.AppUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch

class ProviderServicesFragment : Fragment() {

    private var _binding: FragmentProviderServicesBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ProviderServiceAdapter
    private var currentProviderId: Int = 0

    // Variable to hold the selected image path
    private var selectedImagePath: String? = null
    private lateinit var dialogBinding: DialogServiceEditorBinding // Keep reference to update UI

    // Image Picker Launcher
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val path = AppUtils.saveImageToInternalStorage(requireContext(), it)
            selectedImagePath = path
            // Update the Dialog's ImageView
            if (::dialogBinding.isInitialized && path != null) {
                dialogBinding.serviceImagePreview.setImageURI(Uri.parse(path))
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProviderServicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireActivity().getSharedPreferences("ShineAutoPrefs", AppCompatActivity.MODE_PRIVATE)
        currentProviderId = prefs.getInt("USER_ID", -1)

        adapter = ProviderServiceAdapter(
            onEditClick = { service -> showAddEditDialog(service) },
            onDeleteClick = { service -> deleteService(service) }
        )
        binding.servicesRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.servicesRecyclerView.adapter = adapter

        loadServices()

        binding.fabAddService.setOnClickListener {
            showAddEditDialog(null)
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
            loadServices()
        }
    }

    private fun showAddEditDialog(serviceToEdit: ServiceItem?) {
        val dialog = BottomSheetDialog(requireContext())
        dialogBinding = DialogServiceEditorBinding.inflate(layoutInflater) // Initialize the binding
        dialog.setContentView(dialogBinding.root)

        // Reset image path
        selectedImagePath = null

        if (serviceToEdit != null) {
            dialogBinding.editServiceName.setText(serviceToEdit.name)
            dialogBinding.editServicePrice.setText(serviceToEdit.price.toString())
            dialogBinding.editServiceDesc.setText(serviceToEdit.description)
            dialogBinding.editServiceRegion.setText(serviceToEdit.region)
            dialogBinding.btnSaveService.text = "Update Service"

            // Load existing image
            selectedImagePath = serviceToEdit.imageUri
            if (selectedImagePath != null) {
                dialogBinding.serviceImagePreview.setImageURI(Uri.parse(selectedImagePath))
            }
        }

        // Handle Image Selection
        dialogBinding.btnSelectImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        dialogBinding.btnSaveService.setOnClickListener {
            val name = dialogBinding.editServiceName.text.toString()
            val price = dialogBinding.editServicePrice.text.toString().toDoubleOrNull() ?: 0.0
            val desc = dialogBinding.editServiceDesc.text.toString()
            val region = dialogBinding.editServiceRegion.text.toString()

            if (name.isNotEmpty() && region.isNotEmpty()) {
                lifecycleScope.launch {
                    val dao = ShineAutoDatabase.getDatabase(requireContext()).serviceDao()

                    if (serviceToEdit == null) {
                        val newService = ServiceItem(
                            providerId = currentProviderId,
                            name = name,
                            price = price,
                            description = desc,
                            region = region,
                            imageUri = selectedImagePath // Save the image path
                        )
                        dao.addService(newService)
                    } else {
                        val updatedService = serviceToEdit.copy(
                            name = name,
                            price = price,
                            description = desc,
                            region = region,
                            imageUri = selectedImagePath ?: serviceToEdit.imageUri // Keep old image if no new one selected
                        )
                        dao.updateService(updatedService)
                    }
                    dialog.dismiss()
                    loadServices()
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