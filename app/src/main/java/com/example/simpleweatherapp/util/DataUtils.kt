package com.example.simpleweatherapp.util

import com.example.simpleweatherapp.model.Failure
import com.example.simpleweatherapp.model.Result
import retrofit2.Response
import retrofit2.Retrofit

object DataUtils {

    suspend fun <T> getResponse(
        request: suspend () -> Response<T>,
        errorResponseMessage: String,
        exceptionMessage: String,
        retrofit: Retrofit
    ): Result<T> = try {
        val result = request.invoke()
        if (result.isSuccessful) {
            Result.Success(result.body()!!)
        } else {
            var errorResponse: Failure = FailureUtils.parseError(result, retrofit)!!
            if (errorResponse.status_message == null) {
                errorResponse = Failure(status_message = errorResponseMessage)
            }
            Result.Error(errorResponse)
        }
    } catch (e: Throwable) {
        Result.Error(Failure(status_message = "$exceptionMessage $e"))
    }
}