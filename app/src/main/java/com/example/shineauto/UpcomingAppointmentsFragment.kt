package com.example.shineauto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shineauto.databinding.FragmentUpcomingAppointmentsBinding
import com.example.shineauto.databinding.ItemAppointmentBinding

class UpcomingAppointmentsFragment : Fragment() {

    private var _binding: FragmentUpcomingAppointmentsBinding? = null
    private val binding get() = _binding!!
    private lateinit var appointmentAdapter: AppointmentAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUpcomingAppointmentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        appointmentAdapter.updateAppointments(AppointmentRepository.appointments)
    }

    private fun setupRecyclerView() {
        appointmentAdapter = AppointmentAdapter(mutableListOf())
        binding.upcomingRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = appointmentAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class AppointmentAdapter(private var appointments: MutableList<Appointment>) :
    RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val binding = ItemAppointmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AppointmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        holder.bind(appointments[position])
    }

    override fun getItemCount() = appointments.size

    fun updateAppointments(newAppointments: List<Appointment>) {
        appointments.clear()
        appointments.addAll(newAppointments)
        notifyDataSetChanged()
    }

    class AppointmentViewHolder(private val binding: ItemAppointmentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(appointment: Appointment) {
            binding.serviceNameText.text = appointment.service.title
            "Address: ${appointment.address}".also { binding.appointmentAddressText.text = it }
            "Date: ${appointment.date}".also { binding.appointmentDateText.text = it }
            "Time: ${appointment.time}".also { binding.appointmentTimeText.text = it }
        }
    }
}