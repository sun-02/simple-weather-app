package com.example.simpleweatherapp.model

sealed class Result<T : Any> {

    class Success<T : Any>(val data: T) : Result<T>()
    class Error<T : Any>(val status_code: Int = 0,
                              val status_message: String? = null) : Result<T>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error<*> -> "Error[status_code=$status_code, status_message=$status_message]"
        }
    }
}

val Result<*>.succeeded
    get() = this is Result.Success && data != null
