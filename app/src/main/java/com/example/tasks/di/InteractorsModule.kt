package com.example.tasks.di

import com.example.tasks.BaseApplication
import com.example.tasks.dataSource.cache.TodoDao
import com.example.tasks.dataSource.cache.UserDao
import com.example.tasks.dataSource.cache.mapper.TodoEntityMapper
import com.example.tasks.dataSource.cache.mapper.UserEntityMapper
import com.example.tasks.interactors.auth.AuthInteractors
import com.example.tasks.interactors.auth.CheckAuthenticatedUser
import com.example.tasks.interactors.auth.LoginWithGoogle
import com.example.tasks.interactors.todo.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object InteractorsModule {

    @ViewModelScoped
    @Provides
    fun provideAuthInteractors(
        userDao: UserDao,
        cacheMapper: UserEntityMapper,
        application: BaseApplication
    ): AuthInteractors {
        return AuthInteractors(
            LoginWithGoogle(userDao, cacheMapper),
            CheckAuthenticatedUser(application, userDao, cacheMapper)
        )
    }


    @ViewModelScoped
    @Provides
    fun provideTodoInteractors(todoDao: TodoDao, cacheMapper: TodoEntityMapper): TodoInteractors {
        return TodoInteractors(
            CreateTodo(todoDao, cacheMapper),
            UpdateTodo(todoDao, cacheMapper),
            DeleteTodo(todoDao, cacheMapper),
            GetTodos(todoDao, cacheMapper)
        )
    }
}