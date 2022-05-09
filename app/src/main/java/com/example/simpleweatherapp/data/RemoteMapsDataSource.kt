package com.example.simpleweatherapp.data

import com.example.simpleweatherapp.model.Result
import com.example.simpleweatherapp.model.bingmaps.BingMapsService
import com.example.simpleweatherapp.model.bingmaps.ShortLocation
import com.example.simpleweatherapp.util.DataUtils
import retrofit2.Retrofit

class RemoteMapsDataSource(
    private val retrofit: Retrofit
) {
    private val service = retrofit.create(BingMapsService::class.java)

    suspend fun fetchLocationsByName(name: String): Result<List<ShortLocation>> =
        DataUtils.getResponse(
            { service.fetchLocationsByName(name) },
            "Failed to fetch locations list by name",
            "Locations fetch by name caused an exception",
            retrofit
        )

    suspend fun fetchLocationByCoords(
        latitude: Double,
        longitude: Double
    ): Result<ShortLocation> = DataUtils.getResponse(
            { service.fetchLocationByCoords(latitude, longitude) },
            "Failed to fetch location by coordinates",
            "Location fetch by coordinates caused an exception",
            retrofit
        )
}
