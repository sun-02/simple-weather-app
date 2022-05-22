package com.example.simpleweatherapp.data.bingmaps

import com.example.simpleweatherapp.model.Result
import com.example.simpleweatherapp.model.bingmaps.ShortLocation
import com.example.simpleweatherapp.util.DataUtils
import retrofit2.Retrofit

class RemoteMapsDataSource(
    private val retrofit: Retrofit
) {
    private val service = retrofit.create(BingMapsService::class.java)

    suspend fun fetchLocationsByName(name: String): Result<List<ShortLocation>> =
        DataUtils.getResponse { service.fetchLocationsByName(name) }

    suspend fun fetchLocationByCoords(
        latitude: Double,
        longitude: Double
    ): Result<List<ShortLocation>> =
        DataUtils.getResponse { service.fetchLocationByCoords(latitude, longitude) }
}
