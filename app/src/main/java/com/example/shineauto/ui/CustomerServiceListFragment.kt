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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.shineauto.R
import com.example.shineauto.databinding.DialogBookServiceBinding
import com.example.shineauto.databinding.FragmentCustomerServiceListBinding
import com.example.shineauto.databinding.ItemCarWashServiceBinding
import com.example.shineauto.ui.CustomerBookingsFragment
import java.util.Calendar
import java.util.Locale

// 1. Data class to represent a single car wash service
data class CarWashService(
    val title: String,
    val description: String,
    val price: Double,
    val imageResId: Int // Using a drawable resource ID for the image
)

// Data class for the appointment
data class Appointment(
    val service: CarWashService,
    val address: String,
    val date: String,
    val time: String
)

// Global list to hold appointments
object AppointmentRepository {
    val appointments = mutableListOf<Appointment>()
}

// 2. The Fragment
class CustomerServiceListFragment : Fragment() {
    private var _binding: FragmentCustomerServiceListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using the binding class'''s fully qualified name
        _binding = FragmentCustomerServiceListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 3. Create sample data
        val services = listOf(
            CarWashService("Standard Car Wash", "A thorough exterior wash.", 25.00, R.drawable.standard_wash),
            CarWashService("Premium Car Wash", "A standard wash plus wax and tire shine.", 40.00, R.drawable.premium_wash),
            CarWashService("Wax and Polish", "A protective layer of wax and a high-gloss polish.", 60.00, R.drawable.polish),
            CarWashService("Interior Detailing & Cleaning", "Complete interior vacuum, wipe down, and window cleaning.", 75.00, R.drawable.interior_cleaning)
        )

        // 4. Set up the RecyclerView
        val adapter = CarWashServiceAdapter(services) { service ->
            showBookingDialog(service)
        }
        binding.servicesRecyclerView.adapter = adapter
    }

    private fun showBookingDialog(service: CarWashService) {
        val dialogBinding = DialogBookServiceBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        val formattedString = String.format(Locale.ROOT, "%.2f", service.price)


        dialogBinding.dialogServiceTitle.text = service.title
        dialogBinding.dialogServiceDescription.text = service.description
        "Amount: $${formattedString}".also { dialogBinding.amountText.text = it }

        dialogBinding.dateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                dialogBinding.dateInput.setText(date)
            }, year, month, day).show()
        }

        dialogBinding.timeInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
                val time = String.format("%02d:%02d", selectedHour, selectedMinute)
                dialogBinding.timeInput.setText(time)
            }, hour, minute, true).show()
        }
        
        val bookButton = Button(requireContext())
        bookButton.text = "Book Now"
        bookButton.setOnClickListener {
            val address = dialogBinding.addressInput.text.toString()
            val date = dialogBinding.dateInput.text.toString()
            val time = dialogBinding.timeInput.text.toString()

            if (address.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty()) {
                val newAppointment = Appointment(service, address, date, time)
                AppointmentRepository.appointments.add(newAppointment)
                Toast.makeText(requireContext(), "Booking successful!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()

                (activity as? MainActivity)?.navigateToFragment(CustomerBookingsFragment())
            }
        }
        (dialogBinding.root as ViewGroup).addView(bookButton)
        dialog.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}

// 5. RecyclerView Adapter
class CarWashServiceAdapter(
    private val services: List<CarWashService>,
    private val onServiceSelected: (CarWashService) -> Unit
) :
    RecyclerView.Adapter<CarWashServiceAdapter.ServiceViewHolder>() {

    // Describes an item view and its metadata
    // Use the fully qualified name for the item binding class here as well
    class ServiceViewHolder(private val binding: ItemCarWashServiceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(service: CarWashService, onServiceSelected: (CarWashService) -> Unit) {
            binding.serviceTitle.text = service.title
            binding.serviceImage.setImageResource(service.imageResId)
            binding.root.setOnClickListener {
                onServiceSelected(service)
            }
        }
    }

    // Creates new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val binding = ItemCarWashServiceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ServiceViewHolder(binding)
    }

    // Replaces the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.bind(services[position], onServiceSelected)
    }

    // Returns the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = services.size
}