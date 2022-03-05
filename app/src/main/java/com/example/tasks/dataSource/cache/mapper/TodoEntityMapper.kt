package com.example.tasks.dataSource.cache.mapper

import com.example.tasks.dataSource.cache.entities.TodoEntity
import com.example.tasks.domain.models.Todo
import com.example.tasks.domain.util.DomainMapper

class TodoEntityMapper : DomainMapper<TodoEntity, Todo> {
    override fun mapToDomainModel(model: TodoEntity): Todo {
        return Todo(model.id, model.status, model.task, model.email)
    }

    override fun mapFromDomainModel(domainModel: Todo): TodoEntity {
        return TodoEntity(domainModel.id, domainModel.status, domainModel.task, domainModel.email)
    }

    fun toEntityList(todos: List<Todo>): List<TodoEntity> {
        return todos.map {
            mapFromDomainModel(it)
        }
    }

    fun fromEntityList(todos: List<TodoEntity>): List<Todo> {
        return todos.map {
            mapToDomainModel(it)
        }
    }

}