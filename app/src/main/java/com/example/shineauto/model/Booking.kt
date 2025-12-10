package com.example.shineauto.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey(autoGenerate = true) val bookingId: Int = 0,
    val customerId: Int,
    val providerId: Int,
    val serviceId: Int,
    val serviceName: String, // Stored to avoid complex joins for simple display
    val date: String,
    val time: String,
    val status: String // "PENDING", "ACCEPTED", "COMPLETED", "CANCELLED"
)