package com.example.shineauto

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shineauto.databinding.Fragment3Binding

class Fragment3 : Fragment() {

    private var _binding: Fragment3Binding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = Fragment3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonAboutUs.setOnClickListener {
            // Create an Intent to start AboutUsActivity
            val intent = Intent(activity, AboutUsActivity::class.java)
            startActivity(intent)
        }

        binding.buttonFaqs.setOnClickListener {
            // Create an Intent to start FaqsActivity
            val intent = Intent(activity, FaqsActivity::class.java)
            startActivity(intent)
        }

        binding.buttonContactUs.setOnClickListener {
            // Create an Intent to start ContactUsActivity
            val intent = Intent(activity, ContactUsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}