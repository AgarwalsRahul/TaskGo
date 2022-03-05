package com.example.tasks.domain.util

import com.example.tasks.domain.models.Todo
import java.util.*

object TodoFactory {

    fun createSingleTodo(id: String? = null, task: String, status: Int, email: String): Todo {
        return Todo(id ?: UUID.randomUUID().toString(), status, task, email)
    }
}