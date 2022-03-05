package com.example.tasks.interactors.todo

class TodoInteractors(
     val createTodo: CreateTodo,
     val updateTodo: UpdateTodo,
     val deleteTodo: DeleteTodo,
     val getTodos: GetTodos
) {
}