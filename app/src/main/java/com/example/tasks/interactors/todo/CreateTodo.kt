package com.example.tasks.interactors.todo

import com.example.tasks.dataSource.cache.TodoDao
import com.example.tasks.dataSource.cache.mapper.TodoEntityMapper
import com.example.tasks.domain.data.DataState
import com.example.tasks.domain.models.Todo
import com.example.tasks.domain.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateTodo @Inject constructor(
    private val todoDao: TodoDao,
    private val cacheMapper: TodoEntityMapper
) {
    fun create(todo: Todo, user: User): Flow<DataState<Todo>> = flow {
        emit(DataState.Loading)
        try {
            val result = todoDao.insertTodo(cacheMapper.mapFromDomainModel(todo))
            if (result < 0) {
                emit(DataState.Error("Unable to create task. Please try again."))
            } else {
                val todoFromCache = todoDao.getTodoWithId(user.email, todo.id)
                if (todoFromCache == null) {
                    emit(DataState.Error("Unable to create task. Please try again."))
                } else {
                    emit(DataState.Success(cacheMapper.mapToDomainModel(todoFromCache)))
                }

            }
        } catch (e: Exception) {
            emit(DataState.Error(e.message ?: "Unknown Error!!"))
        }
    }
}