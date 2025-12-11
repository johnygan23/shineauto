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

    // For Customer: Upcoming (PENDING or ACCEPTED)
    @Query("SELECT * FROM bookings WHERE customerId = :customerId AND status IN ('PENDING', 'ACCEPTED')")
    fun getUpcomingBookings(customerId: Int): Flow<List<Booking>>

    // For Customer: History (COMPLETED or CANCELLED)
    @Query("SELECT * FROM bookings WHERE customerId = :customerId AND status IN ('COMPLETED', 'CANCELLED')")
    fun getHistoryBookings(customerId: Int): Flow<List<Booking>>

    // For Provider: Pending Requests
    @Query("SELECT * FROM bookings WHERE providerId = :providerId AND status = 'PENDING'")
    fun getPendingRequests(providerId: Int): Flow<List<Booking>>

    // For Provider: Accepted Orders (To be marked completed)
    @Query("SELECT * FROM bookings WHERE providerId = :providerId AND status = 'ACCEPTED'")
    fun getAcceptedOrders(providerId: Int): Flow<List<Booking>>
}