package com.example.simpleweatherapp.util

import com.example.simpleweatherapp.model.Failure
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException

object FailureUtils {

    fun parseError(response: Response<*>, retrofit: Retrofit): Failure? {
        val converter = retrofit.responseBodyConverter<Failure>(Failure::class.java, arrayOfNulls(0))
        return try {
            converter.convert(response.errorBody()!!)
        } catch (e: IOException) {
            Failure()
        }
    }
}