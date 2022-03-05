package com.example.tasks.ui.todo

sealed class MainStateEvent {
    class GetTodosEvent(val shouldDisplayProgressbar: Boolean = true) : MainStateEvent()
    class UpdateTodoEvent(
        val id: String,
        val status: Int,
        val task: String,
    ) : MainStateEvent()

    class CreateTodoEvent(val status: Int, val task: String) : MainStateEvent()
    class DeleteTodoEvent(val id: String) : MainStateEvent()
}