package com.example.shineauto.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val password: String, // In a real app, hash this!
    val role: String,     // "ADMIN", "CUSTOMER", "PROVIDER"
    val contactInfo: String,
    val profileImageUri: String? = null // For the photo feature later
)