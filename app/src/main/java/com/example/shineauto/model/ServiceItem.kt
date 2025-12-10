package com.example.shineauto.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "services")
data class ServiceItem(
    @PrimaryKey(autoGenerate = true) val serviceId: Int = 0,
    val providerId: Int, // Links to User.id (the provider)
    val name: String,
    val price: Double,
    val description: String,
    val region: String,
    val imageResId: Int = 0 // Using drawable ID for simplicity in coursework
)