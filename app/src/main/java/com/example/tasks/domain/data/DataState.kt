package com.example.tasks.domain.data

sealed class DataState<out R> {

    data class Success<out T>(val data: T,val response:String?=null) : DataState<T>()
    data class Error(val message: String) : DataState<Nothing>()
    object Loading : DataState<Nothing>()
}