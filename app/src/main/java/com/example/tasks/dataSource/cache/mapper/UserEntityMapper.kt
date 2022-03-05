package com.example.tasks.dataSource.cache.mapper

import com.example.tasks.dataSource.cache.entities.UserEntity
import com.example.tasks.domain.models.User
import com.example.tasks.domain.util.DomainMapper

class UserEntityMapper  :DomainMapper<UserEntity,User>  {
    override fun mapToDomainModel(model: UserEntity): User {
        return User(model.email)
    }

    override fun mapFromDomainModel(domainModel: User): UserEntity {
        return UserEntity(domainModel.email)
    }
}