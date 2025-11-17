package com.example.shineauto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.shineauto.databinding.FragmentHistoryAppointmentsBinding

// Placeholder data class
data class HistoryAppointment(val serviceName: String, val date: String, val price: String)

class HistoryAppointmentsFragment : Fragment() {
    private var _binding: FragmentHistoryAppointmentsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryAppointmentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val historyAppointments = listOf(
            HistoryAppointment("Premium Car Wash", "Oct 15, 2025", "$40.00"),
            HistoryAppointment("Wax and Polish", "Sep 2, 2025", "$60.00")
        )
        binding.historyRecyclerView.adapter = HistoryAdapter(historyAppointments)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// Simplified Adapter
class HistoryAdapter(private val items: List<HistoryAppointment>) :
    androidx.recyclerview.widget.RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    class ViewHolder(val binding: com.example.shineauto.databinding.ItemAppointmentHistoryBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        com.example.shineauto.databinding.ItemAppointmentHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.serviceNameText.text = items[position].serviceName
        holder.binding.appointmentDateText.text = "Completed: ${items[position].date}"
        holder.binding.priceText.text = "Price: ${items[position].price}"
    }
    override fun getItemCount() = items.size
}

