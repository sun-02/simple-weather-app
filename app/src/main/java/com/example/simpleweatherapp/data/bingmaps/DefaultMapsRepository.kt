package com.example.simpleweatherapp.data.bingmaps

import com.example.simpleweatherapp.model.bingmaps.ShortLocation
import com.example.simpleweatherapp.model.Result

class DefaultMapsRepository(
    private val mapsDataSource: RemoteMapsDataSource
) : MapsRepository {

    override suspend fun getLocationsList(name: String): Result<List<ShortLocation>> {
        return mapsDataSource.fetchLocationsByName(name)
    }

    override suspend fun getLocation(
        latitude: Double,
        longitude: Double
    ): Result<List<ShortLocation>> {
        return mapsDataSource.fetchLocationByCoords(latitude, longitude)
    }
}
