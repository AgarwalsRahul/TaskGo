package com.example.tasks.dataSource.cache.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.tasks.dataSource.cache.entities.TodoEntity
import com.example.tasks.dataSource.cache.entities.UserEntity

data class UserWithTodos(
    @Embedded val user: UserEntity,
    @Relation(parentColumn = "email", entityColumn = "email")
    val todos: List<TodoEntity>
)