package com.example.simpleweatherapp.util

import com.example.simpleweatherapp.model.Result
import retrofit2.Response
import retrofit2.Retrofit
import timber.log.Timber

object DataUtils {

    suspend fun <T: Any> getResponse(
        request: suspend () -> Response<T>
    ): Result<T> {
        Timber.d("Requesting $request")
        return try {
            val result = request.invoke()
            Timber.d("Got $result")
            val body = result.body()
            if (result.isSuccessful && body != null) {
                Result.Success(body)
            } else {
                Result.Error(result.code(), result.message())
            }
        } catch (e: Throwable) {
            Result.Error(-1, e.toString())
        }
    }
}