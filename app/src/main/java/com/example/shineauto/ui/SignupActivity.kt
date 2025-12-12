package com.example.shineauto.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.shineauto.R
import com.example.shineauto.data.ShineAutoDatabase
import com.example.shineauto.model.User
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val usernameInput = findViewById<EditText>(R.id.signupUsername)
        val passwordInput = findViewById<EditText>(R.id.signupPassword)
        val contactInput = findViewById<EditText>(R.id.signupContact)
        val roleSpinner = findViewById<Spinner>(R.id.roleSpinner)
        val signupButton = findViewById<Button>(R.id.signupButton)
        val loginRedirect = findViewById<TextView>(R.id.loginRedirectText)

        // Setup Role Spinner
        val roles = arrayOf("CUSTOMER", "PROVIDER", "ADMIN") //Removed "ADMIN" to prevent user to create ADMIN account
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)
        roleSpinner.adapter = adapter

        signupButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            val contact = contactInput.text.toString()
            val role = roleSpinner.selectedItem.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                val newUser = User(username = username, password = password, role = role, contactInfo = contact)

                // Using Coroutine to Insert into Database
                lifecycleScope.launch {
                    ShineAutoDatabase.getDatabase(this@SignupActivity).userDao().registerUser(newUser)
                    Toast.makeText(this@SignupActivity, "Account Created! Please Login.", Toast.LENGTH_SHORT).show()
                    finish() // Go back to Login Activity
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        loginRedirect.setOnClickListener {
            finish()
        }
    }
}