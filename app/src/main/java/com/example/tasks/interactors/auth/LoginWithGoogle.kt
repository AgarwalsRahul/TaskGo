package com.example.tasks.interactors.auth

import com.example.tasks.dataSource.cache.UserDao
import com.example.tasks.dataSource.cache.mapper.UserEntityMapper
import com.example.tasks.domain.data.DataState
import com.example.tasks.domain.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginWithGoogle @Inject constructor(
    private val userDao: UserDao,
    private val cacheMapper: UserEntityMapper
) {

    fun login(user: User): Flow<DataState<User>> = flow {
        emit(DataState.Loading)
        try {
            val result = userDao.insertUser(cacheMapper.mapFromDomainModel(user))

            emit(DataState.Success(user))

        } catch (e: Exception) {
            emit(DataState.Error(e.message ?: "UNKNOWN ERROR!!"))
        }
    }
}