package com.example.tasks.ui.todo

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasks.domain.data.DataState
import com.example.tasks.domain.models.Todo
import com.example.tasks.domain.models.User
import com.example.tasks.domain.util.TodoFactory
import com.example.tasks.interactors.todo.TodoInteractors
import com.example.tasks.interactors.todo.UpdateTodo
import com.example.tasks.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val todoInteractors: TodoInteractors,
    private val todoFactory: TodoFactory
) : ViewModel() {

    private val _dataState = MutableLiveData<DataState<List<Todo>?>>()
    val dataState: LiveData<DataState<List<Todo>?>>
        get() = _dataState

    private var page: Int = 1;
    var layoutManagerState: Parcelable? = null
    private var isQueryExhausted: Boolean = true;

    @OptIn(DelicateCoroutinesApi::class)
    fun setStateEvent(stateEvent: MainStateEvent) {
        sessionManager.cachedUser.value?.let {
            viewModelScope.launch {
                when (stateEvent) {
                    is MainStateEvent.CreateTodoEvent -> {
                        todoInteractors.createTodo.create(
                            todoFactory.createSingleTodo(
                                task = stateEvent.task,
                                status = stateEvent.status,
                                email = it.email
                            ),
                            it
                        ).launchIn(viewModelScope)
                    }
                    is MainStateEvent.GetTodosEvent -> {
                        todoInteractors.getTodos.execute(it.email, page).onEach {
                            dataState ->  _dataState.value = dataState
                        }
                            .launchIn(viewModelScope)
                    }
                    is MainStateEvent.UpdateTodoEvent->{
                        todoInteractors.updateTodo.execute(TodoFactory.createSingleTodo(
                            stateEvent.id,
                            stateEvent.task,
                            stateEvent.status,
                            it.email,
                        ),it).launchIn(viewModelScope)
                    }
                    is MainStateEvent.DeleteTodoEvent->{
                        todoInteractors.deleteTodo.execute(stateEvent.id,it).onEach {
                            dataState -> _dataState.value=dataState
                        }
                            .launchIn(viewModelScope)
                    }
                }
            }
        } ?: sessionManager.logout()
    }

    fun nextPage() {
        if (!isQueryExhausted) {
            clearLayoutManagerState()
            incrementPage()
            setStateEvent(MainStateEvent.GetTodosEvent(false))
        }

    }

    private fun incrementPage() {
        page++
    }

    private fun resetPage() {
        page = 1
    }

    fun clearLayoutManagerState() {
        layoutManagerState = null
    }

    fun loadFirstPage() {
        isQueryExhausted = false
        resetPage()
        clearLayoutManagerState()
        setStateEvent(MainStateEvent.GetTodosEvent())
    }

    fun getPage() = page


    fun setQueryExhausted(exhausted: Boolean) {
        isQueryExhausted = exhausted
    }

    fun isQueryExhausted() = isQueryExhausted

    fun setPage(page: Int) {
        this.page = page
    }

    fun refreshQuery(){
        setQueryExhausted(false)
        setStateEvent(MainStateEvent.GetTodosEvent(false))
    }
}