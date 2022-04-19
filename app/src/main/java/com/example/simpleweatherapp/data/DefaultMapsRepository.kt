package com.example.simpleweatherapp.data

import com.example.simpleweatherapp.model.Location
import com.example.simpleweatherapp.data.remote.MapsDataSource
import com.example.simpleweatherapp.model.Locations
import com.example.simpleweatherapp.model.Result

class DefaultMapsRepository(
    private val mapsDataSource: MapsDataSource
) : MapsRepository {
    override suspend fun getLocations(query: String): Result<Locations> {
        return mapsDataSource.fetchLocations(query)
    }
}
