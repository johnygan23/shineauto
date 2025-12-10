package com.example.shineauto.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.shineauto.ui.MainActivity
import com.example.shineauto.databinding.FragmentProfileBinding // Make sure this matches your XML name

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ... existing code for profile setup ...

        // --- THE LOGOUT UPDATE ---
        // Assuming your logout button ID in XML is 'logoutButton' or 'logoutLayout'
        // Check your fragment_profile.xml to be sure of the ID!
        binding.logoutButton.setOnClickListener {
            // Call the logout function from the parent MainActivity
            (activity as? MainActivity)?.logout()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}