package com.nibmz7gmail.sgprayertimemusollah.core.result

import com.nibmz7gmail.sgprayertimemusollah.core.result.Result.Success

sealed class Result<out R> {

    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val errorType: Enum<*>) : Result<Nothing>()
    object Loading : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$errorType]"
            Loading -> "Loading"
        }
    }
}

val Result<*>.succeeded
    get() = this is Success && data != null