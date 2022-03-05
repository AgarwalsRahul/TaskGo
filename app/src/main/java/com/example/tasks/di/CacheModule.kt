package com.example.tasks.di

import androidx.room.Room
import com.example.tasks.BaseApplication
import com.example.tasks.dataSource.cache.Database
import com.example.tasks.dataSource.cache.TodoDao
import com.example.tasks.dataSource.cache.UserDao
import com.example.tasks.dataSource.cache.entities.UserEntity
import com.example.tasks.dataSource.cache.mapper.TodoEntityMapper
import com.example.tasks.dataSource.cache.mapper.UserEntityMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CacheModule {

    @Singleton
    @Provides
    fun provideDb(app: BaseApplication): Database {
        return Room
            .databaseBuilder(app, Database::class.java, Database.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideTodoDao(db: Database): TodoDao{
        return db.todoDao
    }

    @Singleton
    @Provides
    fun provideUserDao(db: Database): UserDao{
        return db.userDao
    }

    @Singleton
    @Provides
    fun provideCacheUserMapper(): UserEntityMapper {
        return UserEntityMapper()
    }

    @Singleton
    @Provides
    fun provideCacheTodoMapper(): TodoEntityMapper {
        return TodoEntityMapper()
    }


}