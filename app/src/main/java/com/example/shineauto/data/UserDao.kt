package com.example.shineauto.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.shineauto.model.User

@Dao
interface UserDao {
    @Insert
    suspend fun registerUser(user: User): Long

    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    suspend fun login(username: String, password: String): User?

    @Query("SELECT * FROM users WHERE role = 'PROVIDER'")
    suspend fun getAllProviders(): List<User>

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User> // For Admin

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Int): User?

}