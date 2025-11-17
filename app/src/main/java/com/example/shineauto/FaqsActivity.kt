package com.example.shineauto

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.shineauto.databinding.ActivityFaqsBinding

class FaqsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFaqsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaqsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}