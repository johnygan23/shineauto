package com.example.shineauto.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.shineauto.model.ServiceItem

@Dao
interface ServiceDao {
    @Insert
    suspend fun addService(service: ServiceItem)

    @Update
    suspend fun updateService(service: ServiceItem)

    @Delete
    suspend fun deleteService(service: ServiceItem)

    @Query("SELECT * FROM services")
    fun getAllServices(): List<ServiceItem> // Used by Customers

    @Query("SELECT * FROM services WHERE providerId = :providerId")
    suspend fun getServicesByProvider(providerId: Int): List<ServiceItem>
}