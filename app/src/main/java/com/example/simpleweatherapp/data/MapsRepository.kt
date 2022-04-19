package com.example.simpleweatherapp.data

import com.example.simpleweatherapp.model.Location
import com.example.simpleweatherapp.model.Locations
import com.example.simpleweatherapp.model.Result

interface MapsRepository {
    suspend fun getLocations(query: String): Result<Locations>
}
