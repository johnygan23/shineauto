package com.example.shineauto.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shineauto.data.ShineAutoDatabase
import com.example.shineauto.databinding.DialogBookServiceBinding
import com.example.shineauto.databinding.FragmentCustomerServiceListBinding
import com.example.shineauto.databinding.ItemCarWashServiceBinding
import com.example.shineauto.model.Booking
import com.example.shineauto.model.ServiceItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Locale

class CustomerServiceListFragment : Fragment() {

    private var _binding: FragmentCustomerServiceListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CustomerServiceAdapter
    private var currentCustomerId: Int = 0
    private lateinit var currentCustomerUsername: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomerServiceListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Get Logged-in Customer ID and Name
        val prefs = requireActivity().getSharedPreferences("ShineAutoPrefs", AppCompatActivity.MODE_PRIVATE)
        currentCustomerId = prefs.getInt("USER_ID", -1)
        currentCustomerUsername = prefs.getString("USERNAME", "Unknown Name") ?: "Unknown Customer"

        // 2. Setup RecyclerView
        adapter = CustomerServiceAdapter { service ->
            showBookingDialog(service)
        }
        binding.servicesRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.servicesRecyclerView.adapter = adapter

        // 3. Load Services from Database (Real Data)
        loadServices()
    }

    private fun loadServices() {
        lifecycleScope.launch {
            // Fetch on Background Thread (IO)
            val services = withContext(Dispatchers.IO) {
                ShineAutoDatabase.getDatabase(requireContext()).serviceDao().getAllServices()
            }
            // Update UI on Main Thread
            adapter.submitList(services)
        }
    }

    private fun showBookingDialog(service: ServiceItem) {
        val dialogBinding = DialogBookServiceBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        // Populate Dialog with Service Info
        dialogBinding.dialogServiceTitle.text = service.name
        dialogBinding.dialogServiceDescription.text = service.description
        dialogBinding.amountText.text = String.format(Locale.ROOT, "Amount: $%.2f", service.price)

        // Date Picker
        dialogBinding.dateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, year, month, day ->
                dialogBinding.dateInput.setText("$day/${month + 1}/$year")
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        // Time Picker
        val timeSlots = listOf(
            "08:00", "08:30", "09:00", "09:30", "10:00", "10:30",
            "11:00", "11:30", "12:00", "12:30", "13:00", "13:30",
            "14:00", "14:30", "15:00", "15:30", "16:00", "16:30",
            "17:00", "17:30", "18:00", "18:30", "19:00", "19:30", "20:00"
        )

        dialogBinding.timeInput.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Select Time")
                .setItems(timeSlots.toTypedArray()) { _, which ->
                    dialogBinding.timeInput.setText(timeSlots[which])
                }
                .show()
        }

        // --- THE INTEGRATION LOGIC ---
        // We create the button programmatically or you can bind it if it's in XML
        // Ideally, add an ID to the button in 'dialog_book_service.xml' like @+id/btnConfirmBooking
        // For now, I'll assume you added the button to the layout or are adding it dynamically like before.

        // Let's assume you added a Button with ID `btnConfirmBooking` to the XML:
        /*
           // IN XML:
           <Button android:id="@+id/btnConfirmBooking" ... text="Book Now" />
        */

        val bookButton = Button(requireContext())
        bookButton.text = "Confirm Booking"
        bookButton.setOnClickListener {
            val date = dialogBinding.dateInput.text.toString()
            val time = dialogBinding.timeInput.text.toString()

            if (date.isNotEmpty() && time.isNotEmpty()) {
                saveBookingToDatabase(service, date, time)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Please select date and time", Toast.LENGTH_SHORT).show()
            }
        }
        (dialogBinding.root as ViewGroup).addView(bookButton)

        dialog.show()
    }

    private fun saveBookingToDatabase(service: ServiceItem, date: String, time: String) {
        lifecycleScope.launch {
            val newBooking = Booking(
                customerId = currentCustomerId,
                customerName = currentCustomerUsername,
                providerId = service.providerId,
                serviceId = service.serviceId,
                serviceName = service.name,
                date = date,
                time = time,
                status = "PENDING" // Default status
            )

            ShineAutoDatabase.getDatabase(requireContext()).bookingDao().createBooking(newBooking)
            Toast.makeText(context, "Booking Sent to Provider!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// --- UPDATED ADAPTER ---
class CustomerServiceAdapter(
    private val onServiceClick: (ServiceItem) -> Unit
) : RecyclerView.Adapter<CustomerServiceAdapter.ServiceViewHolder>() {

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
            // Note: Update IDs (serviceTitle, servicePrice) to match your XML
            binding.serviceTitle.text = service.name
            // If you have a price text view in your item layout:
            // binding.servicePrice.text = "$${service.price}"

            // Using placeholder image for now
            binding.serviceImage.setImageResource(com.example.shineauto.R.drawable.standard_wash)

            binding.root.setOnClickListener {
                onServiceClick(service)
            }
        }
    }
}