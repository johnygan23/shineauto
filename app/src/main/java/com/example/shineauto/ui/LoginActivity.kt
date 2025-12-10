package com.example.shineauto.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.shineauto.ui.MainActivity
import com.example.shineauto.R
import com.example.shineauto.data.ShineAutoDatabase
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernameInput = findViewById<EditText>(R.id.loginUsername)
        val passwordInput = findViewById<EditText>(R.id.loginPassword)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signupRedirect = findViewById<TextView>(R.id.signupRedirectText)

        loginButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                lifecycleScope.launch {
                    val user = ShineAutoDatabase.getDatabase(this@LoginActivity).userDao().login(username, password)

                    if (user != null) {
                        // SAVE SESSION
                        val sharedPreferences = getSharedPreferences("ShineAutoPrefs", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putInt("USER_ID", user.id)
                        editor.putString("USER_ROLE", user.role)
                        editor.putString("USERNAME", user.username)
                        editor.apply()

                        Toast.makeText(this@LoginActivity, "Login Successful!", Toast.LENGTH_SHORT).show()

                        // Navigate to Main App
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Invalid Credentials", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        signupRedirect.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}