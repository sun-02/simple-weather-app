package com.example.simpleweatherapp.data.remote

import com.example.simpleweatherapp.model.Location
import com.example.simpleweatherapp.model.Locations
import com.example.simpleweatherapp.model.Result

interface MapsDataSource {
    suspend fun fetchLocations(query: String): Result<Locations>
}
