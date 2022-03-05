package com.example.tasks.dataSource.cache.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "status") // 0 -> Undone // 1 -> Done
    val status: Int,
    @ColumnInfo(name = "task")
    val task: String,
    @ColumnInfo(name="email")
    val email:String
)