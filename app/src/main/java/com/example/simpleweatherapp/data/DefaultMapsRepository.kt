package com.example.simpleweatherapp.data

import com.example.simpleweatherapp.model.bingmaps.ShortLocation
import com.example.simpleweatherapp.model.Result

class DefaultMapsRepository(
    private val mapsDataSource: RemoteMapsDataSource
) : MapsRepository {

    override suspend fun getLocationsByName(name: String): Result<List<ShortLocation>> {
        return mapsDataSource.fetchLocationsByName(name)
    }

    override suspend fun getLocationByCoords(
        latitude: Double,
        longitude: Double
    ): Result<ShortLocation> {
        return mapsDataSource.fetchLocationByCoords(latitude, longitude)
    }
}
