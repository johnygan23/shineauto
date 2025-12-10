package com.example.shineauto.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.shineauto.model.Booking
import com.example.shineauto.model.ServiceItem
import com.example.shineauto.model.User

@Database(entities = [User::class, ServiceItem::class, Booking::class], version = 1, exportSchema = false)
abstract class ShineAutoDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun serviceDao(): ServiceDao
    abstract fun bookingDao(): BookingDao

    companion object {
        @Volatile
        private var INSTANCE: ShineAutoDatabase? = null

        fun getDatabase(context: Context): ShineAutoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ShineAutoDatabase::class.java,
                    "shineauto_database"
                )
                    .fallbackToDestructiveMigration() // Wipes data if you change schema (good for dev)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}