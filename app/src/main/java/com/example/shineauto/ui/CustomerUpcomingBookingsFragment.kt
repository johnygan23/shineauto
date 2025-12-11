package com.example.shineauto.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shineauto.data.ShineAutoDatabase
import com.example.shineauto.databinding.FragmentCustomerUpcomingBookingsBinding
import com.example.shineauto.databinding.ItemAppointmentBinding
import com.example.shineauto.model.Booking
import kotlinx.coroutines.launch

// Rename XML to fragment_customer_upcoming_bookings.xml or keep fragment_upcoming_appointments.xml
class CustomerUpcomingBookingsFragment : Fragment() {

    private var _binding: FragmentCustomerUpcomingBookingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: BookingAdapter
    private var currentCustomerId: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCustomerUpcomingBookingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireActivity().getSharedPreferences("ShineAutoPrefs", AppCompatActivity.MODE_PRIVATE)
        currentCustomerId = prefs.getInt("USER_ID", -1)

        adapter = BookingAdapter()
        // Assuming ID is upcomingRecyclerView in your XML
        binding.upcomingRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.upcomingRecyclerView.adapter = adapter

        // Watch the Database for changes
        lifecycleScope.launch {
            ShineAutoDatabase.getDatabase(requireContext())
                .bookingDao()
                .getUpcomingBookings(currentCustomerId)
                .collect { bookings ->
                    // Filter for active bookings if you want (e.g., status != "COMPLETED")
                    // For now, let's show all
                    adapter.submitList(bookings)
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// --- SIMPLE ADAPTER FOR BOOKINGS ---
class BookingAdapter : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {
    private var items = listOf<Booking>()

    fun submitList(newItems: List<Booking>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = ItemAppointmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class BookingViewHolder(private val binding: ItemAppointmentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(booking: Booking) {
            binding.serviceNameText.text = booking.serviceName
            binding.appointmentDateText.text = "Date: ${booking.date}"
            binding.appointmentTimeText.text = "Time: ${booking.time}"
            binding.appointmentAddressText.text = "Status: ${booking.status}"
            // Reuse address text field for Status for now, or add a status TextView
        }
    }
}