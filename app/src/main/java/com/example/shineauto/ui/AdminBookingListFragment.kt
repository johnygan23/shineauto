package com.example.shineauto.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shineauto.data.ShineAutoDatabase
import com.example.shineauto.databinding.FragmentAdminBookingListBinding
import com.example.shineauto.ui.adapter.AdminBookingAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminBookingListFragment : Fragment() {

    private var _binding: FragmentAdminBookingListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AdminBookingAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdminBookingListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = AdminBookingAdapter()
        binding.adminBookingsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.adminBookingsRecyclerView.adapter = adapter

        loadBookings()
    }

    private fun loadBookings() {
        lifecycleScope.launch {
            val bookings = withContext(Dispatchers.IO) {
                ShineAutoDatabase.getDatabase(requireContext()).bookingDao().getAllBookings()
            }
            adapter.submitList(bookings)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}