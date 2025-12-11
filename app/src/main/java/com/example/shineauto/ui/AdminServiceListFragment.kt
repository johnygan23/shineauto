package com.example.shineauto.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shineauto.data.ShineAutoDatabase
import com.example.shineauto.databinding.FragmentAdminServiceListBinding
import com.example.shineauto.model.ServiceItem
import com.example.shineauto.ui.adapter.AdminServiceAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminServiceListFragment : Fragment() {

    private var _binding: FragmentAdminServiceListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AdminServiceAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdminServiceListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = AdminServiceAdapter { service ->
            confirmDeleteService(service)
        }
        binding.adminServicesRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.adminServicesRecyclerView.adapter = adapter

        loadServices()
    }

    private fun loadServices() {
        lifecycleScope.launch {
            val services = withContext(Dispatchers.IO) {
                ShineAutoDatabase.getDatabase(requireContext()).serviceDao().getAllServices()
            }
            adapter.submitList(services)
        }
    }

    private fun confirmDeleteService(service: ServiceItem) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Service")
            .setMessage("Are you sure you want to delete ${service.name}?")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch {
                    ShineAutoDatabase.getDatabase(requireContext()).serviceDao().deleteService(service)
                    Toast.makeText(context, "Service deleted", Toast.LENGTH_SHORT).show()
                    loadServices()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}