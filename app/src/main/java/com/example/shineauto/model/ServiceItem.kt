package com.example.shineauto.model
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "services")
data class ServiceItem(
    @PrimaryKey(autoGenerate = true) val serviceId: Int = 0,
    val providerId: Int,
    val name: String,
    val price: Double,
    val description: String,
    val imageUri: String? = null
)