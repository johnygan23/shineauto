package com.example.shineauto.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.shineauto.model.Booking
import com.example.shineauto.model.ServiceItem
import com.example.shineauto.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Ensure version is correct. If you changed schema recently, you might need to bump it or uninstall app.
@Database(entities = [User::class, ServiceItem::class, Booking::class], version = 2, exportSchema = false)
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
                    .fallbackToDestructiveMigration()
                    .addCallback(ShineAutoDatabaseCallback(context)) // <--- ADD THIS LINE
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    // --- CALLBACK CLASS FOR DUMMY DATA ---
    private class ShineAutoDatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Perform DB operations on a background thread
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.userDao(), database.serviceDao())
                }
            }
        }

        suspend fun populateDatabase(userDao: UserDao, serviceDao: ServiceDao) {
            // 1. Create Dummy Users
            // We verify IDs manually to link them easily.
            // Note: Password encryption is skipped for simplicity as per your current project structure.

            // Admin Account (ID = 1)
            val admin = User(
                id = 1,
                username = "admin",
                password = "1234",
                role = "ADMIN",
                contactInfo = "admin@shineauto.com",
                profileImageUri = null
            )

            // Provider Account (ID = 2)
            val provider = User(
                id = 2,
                username = "ShineWasher",
                password = "1234",
                role = "PROVIDER",
                contactInfo = "shinewasher@email.com",
                profileImageUri = null
            )

            // Customer Account (ID = 3)
            val customer = User(
                id = 3,
                username = "Ken",
                password = "1234",
                role = "CUSTOMER",
                contactInfo = "ken@email.com",
                profileImageUri = null
            )

            userDao.registerUser(admin)
            userDao.registerUser(provider)
            userDao.registerUser(customer)

            // 2. Create Dummy Services (Linked to Provider ID 2)
            val service1 = ServiceItem(
                providerId = 2, // Must match the Provider's ID above
                name = "Basic Wash",
                price = 30.00,
                description = "Exterior wash and tire shine.",
                imageUri = null
            )

            val service2 = ServiceItem(
                providerId = 2,
                name = "Premium Wash",
                price = 45.00,
                description = "Full exterior wash, interior vacuum, and wax.",
                imageUri = null
            )

            serviceDao.addService(service1)
            serviceDao.addService(service2)
        }
    }
}