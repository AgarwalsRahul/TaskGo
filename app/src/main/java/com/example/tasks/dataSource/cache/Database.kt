package com.example.tasks.dataSource.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tasks.dataSource.cache.entities.TodoEntity
import com.example.tasks.dataSource.cache.entities.UserEntity

@Database(entities = [UserEntity::class,TodoEntity::class,],version=1)
abstract class Database :RoomDatabase(){
    abstract val userDao:UserDao
    abstract val todoDao:TodoDao

    companion object {
        const val DATABASE_NAME: String = "todo_db"
    }
}