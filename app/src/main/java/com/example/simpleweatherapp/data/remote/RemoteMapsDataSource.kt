package com.example.simpleweatherapp.data.remote

import com.example.simpleweatherapp.BuildConfig
import com.example.simpleweatherapp.model.*
import com.example.simpleweatherapp.model.Result.Success
import com.example.simpleweatherapp.model.Result.Error
import com.example.simpleweatherapp.util.FailureUtils
import retrofit2.Response
import retrofit2.Retrofit

class RemoteMapsDataSource(
    private val retrofit: Retrofit
) : MapsDataSource {
    override suspend fun fetchLocations(query: String): Result<Locations> {
        val service = retrofit.create(BingMapsService::class.java)
        return getResponse(
            { service.fetchLocations(query, BuildConfig.BING_MAPS_KEY) },
            "Failed to fetch location by name"
        )

//        val success = Success(
//            Locations(
//                listOf(
//                    Location(
//                        "test",
//                        "test",
//                        "test",
//                        "test",
//                        Coords(0.0, 0.0)
//                    )
//                )
//            )
//        )
//        return success

//        val error = Error(Failure(status_message = "Failed to fetch location by name"))
//        return error
    }

    private suspend fun <T> getResponse(
        request: suspend () -> Response<T>,
        defaultErrorMessage: String
    ): Result<T> = try {
        val result = request.invoke()
        if (result.isSuccessful) {
            Success(result.body()!!)
        } else {
            var errorResponse: Failure = FailureUtils.parseError(result, retrofit)!!
            if (errorResponse.status_message == null) {
                errorResponse = Failure(status_message = defaultErrorMessage)
            }
            Error(errorResponse)
        }
    } catch (e: Throwable) {
        Error(Failure(status_message = "Location fetch caused an exception $e"))
    }
}
