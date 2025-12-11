package com.example.shineauto.model
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey(autoGenerate = true) val bookingId: Int = 0,
    val customerId: Int,
    val customerName: String,
    val providerId: Int,
    val serviceId: Int,
    val serviceName: String,
    val date: String,
    val time: String,
    val status: String
)