package com.example.simpleweatherapp.model

sealed class Result<out R> {

    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val failure: Failure) : Result<Nothing>()
    object Loading : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[failure=$failure]"
            Loading -> "Loading"
        }
    }
}

val Result<*>.succeeded
    get() = this is Result.Success && data != null