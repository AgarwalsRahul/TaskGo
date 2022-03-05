package com.example.tasks.interactors.todo

import com.example.tasks.dataSource.cache.TodoDao
import com.example.tasks.dataSource.cache.mapper.TodoEntityMapper
import com.example.tasks.domain.data.DataState
import com.example.tasks.domain.models.Todo
import com.example.tasks.domain.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteTodo @Inject constructor(
    private val todoDao: TodoDao,
    private val cacheMapper: TodoEntityMapper
) {
    fun execute(id: String, user: User): Flow<DataState<List<Todo>?>> = flow {
        emit(DataState.Loading)
        try {
            val result = todoDao.deleteTodo(id)
            if (result < 0) {
                emit(DataState.Error("Unable to delete task. Please try again."))
            } else {

                emit(DataState.Success(null, response = "Task is successfully deleted."))


            }
        } catch (e: Exception) {
            emit(DataState.Error(e.message ?: "Unknown Error!!"))
        }
    }
}