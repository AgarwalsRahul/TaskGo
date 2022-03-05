package com.example.tasks.interactors.todo

import com.example.tasks.dataSource.cache.TodoDao
import com.example.tasks.dataSource.cache.mapper.TodoEntityMapper
import com.example.tasks.domain.data.DataState
import com.example.tasks.domain.models.Todo
import com.example.tasks.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetTodos @Inject constructor(
    private val todoDao: TodoDao,
    private val cacheMapper: TodoEntityMapper
) {

    fun execute(email: String, page: Int): Flow<DataState<List<Todo>>> = flow {
        emit(DataState.Loading)
        try {
            todoDao.getTodos(email, page, Constants.PAGE_SIZE).collect {
                emit(DataState.Success(cacheMapper.fromEntityList(it.todos)))
            }

        } catch (e: Exception) {
            emit(DataState.Error(e.message ?: "Unknown Error!!"))
        }
    }
}