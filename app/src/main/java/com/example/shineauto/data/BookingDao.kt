package com.example.shineauto.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.shineauto.model.Booking
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {
    @Insert
    suspend fun createBooking(booking: Booking)

    @Update
    suspend fun updateBooking(booking: Booking)

    // Using Flow for live updates (Customer History)
    @Query("SELECT * FROM bookings WHERE customerId = :customerId ORDER BY bookingId DESC")
    fun getBookingsForCustomer(customerId: Int): Flow<List<Booking>>

    // Using Flow for live updates (Provider Dashboard)
    @Query("SELECT * FROM bookings WHERE providerId = :providerId AND status = 'PENDING'")
    fun getPendingRequestsForProvider(providerId: Int): Flow<List<Booking>>
}