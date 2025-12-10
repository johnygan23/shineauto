package com.example.shineauto.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.shineauto.databinding.FragmentInformationBinding

class InformationFragment : Fragment() {

    private var _binding: FragmentInformationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonAboutUs.setOnClickListener {
            val intent = Intent(requireActivity(), AboutUsActivity::class.java)
            startActivity(intent)
        }

        binding.buttonFaqs.setOnClickListener {
            val intent = Intent(requireActivity(), FaqsActivity::class.java)
            startActivity(intent)
        }

        binding.buttonContactUs.setOnClickListener {
            val intent = Intent(requireActivity(), ContactUsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}