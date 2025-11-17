package com.example.shineauto

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.shineauto.databinding.ActivityContactUsBinding // Import ViewBinding
import androidx.core.net.toUri

class ContactUsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContactUsBinding // Declare binding variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactUsBinding.inflate(layoutInflater) // Inflate layout
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Set phone number and email for intents
        val phoneNumber = "012-3338888"

        // --- Call Button Logic ---
        binding.buttonCall.setOnClickListener {
            // Create an intent to dial the number
            val callIntent = Intent(Intent.ACTION_DIAL).apply {
                data = "tel:$phoneNumber".toUri()
            }
            // Start the activity, which will open the user's phone dialer
            startActivity(callIntent)
        }
    }
}
