package com.example.tasks.di

import android.content.Context
import com.example.tasks.BaseApplication
import com.example.tasks.domain.util.TodoFactory
import com.example.tasks.util.SessionManager
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideApplication(@ApplicationContext app: Context): BaseApplication {
        return app as BaseApplication
    }

    @Provides
    @Singleton
    fun provideSessionManger(app: BaseApplication,googleSignInOptions: GoogleSignInOptions): SessionManager {
        return SessionManager(app,googleSignInOptions)
    }

    @Provides
    @Singleton
    fun provideTodoFactory(): TodoFactory {
        return TodoFactory
    }
}