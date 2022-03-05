package com.example.tasks.ui.auth

import androidx.lifecycle.*
import com.example.tasks.domain.data.DataState
import com.example.tasks.domain.models.User
import com.example.tasks.interactors.auth.AuthInteractors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authInteractors: AuthInteractors,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _dataState = MutableLiveData<DataState<User?>>()
    val dataState: LiveData<DataState<User?>>
        get() = _dataState

    fun setStateEvent(stateEvent: LoginStateEvent) {
        viewModelScope.launch {
            when (stateEvent) {
                is LoginStateEvent.CheckSignedInUserEvent -> {
                    authInteractors.checkAuthenticatedUser.check()
                        .onEach { dataState ->
                            _dataState.value = dataState
                        }
                        .launchIn(viewModelScope)
                }
                is LoginStateEvent.LoginEvent -> {
                    authInteractors.loginWithGoogle.login(User(stateEvent.email))
                        .onEach { dataState ->
                            _dataState.value = dataState
                        }
                        .launchIn(viewModelScope)
                }

            }
        }
    }

}