package com.example.tasks.interactors.auth

import com.example.tasks.BaseApplication
import com.example.tasks.dataSource.cache.UserDao
import com.example.tasks.dataSource.cache.entities.UserEntity
import com.example.tasks.dataSource.cache.mapper.UserEntityMapper
import com.example.tasks.domain.data.DataState
import com.example.tasks.domain.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import javax.inject.Inject

class CheckAuthenticatedUser @Inject constructor(
    private val application: BaseApplication,
    private val userDao: UserDao,
    private val cacheMapper: UserEntityMapper
) {

    fun check(): Flow<DataState<User?>> = flow {
        emit(DataState.Loading)
        try {
            val account = GoogleSignIn.getLastSignedInAccount(application)
            if (account != null && account.email != null) {
                val cachedUser = userDao.retrieveUser(account.email!!)
                if (cachedUser == null) {
                    userDao.insertUser(UserEntity(account.email!!))
                }
                emit(DataState.Success(User(account.email!!)))
            } else {
                emit(DataState.Success(null))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e.message ?: "UNKNOWN ERROR!!"))
        }
    }
}