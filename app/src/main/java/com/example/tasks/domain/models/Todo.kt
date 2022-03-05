package com.example.tasks.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Todo(
    val id: String,
    val status: Int, // 0 -> Undone // 1 -> Done
    val task: String,
    val email:String
):Parcelable