package com.example.tasks.dataSource.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tasks.dataSource.cache.entities.UserEntity

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE email =:email")
    suspend fun retrieveUser(email: String): UserEntity?

}