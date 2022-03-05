package com.example.tasks.ui.auth

sealed class LoginStateEvent {

    object CheckSignedInUserEvent:LoginStateEvent()
    class LoginEvent(val email:String):LoginStateEvent()
}