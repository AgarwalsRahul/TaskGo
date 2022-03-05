package com.example.tasks.dataSource.cache

import androidx.room.*
import com.example.tasks.dataSource.cache.entities.TodoEntity
import com.example.tasks.dataSource.cache.relations.UserWithTodos
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todoEntity: TodoEntity): Long

    @Query("DELETE FROM todos WHERE id=:todoId")
    suspend fun deleteTodo(todoId: String):Int

    @Transaction
    @Query("SELECT * FROM users WHERE email=:email LIMIT (:page* :pageSize)")
    fun getTodos(email: String, page: Int, pageSize: Int): Flow<UserWithTodos>

    @Query("SELECT * FROM todos WHERE email=:email AND id=:id")
    suspend fun getTodoWithId(email: String, id: String): TodoEntity?

    @Query("UPDATE todos SET task=:task, status=:status WHERE id=:id AND email=:email")
    suspend fun updateTodo(email: String, id: String, task: String,status:Int): Int
}