package com.example.simpleweatherapp.data

import com.example.simpleweatherapp.model.bingmaps.ShortLocation
import com.example.simpleweatherapp.model.Result

interface MapsRepository {
    suspend fun getLocationsByName(name: String): Result<List<ShortLocation>>
    suspend fun getLocationByCoords(latitude: Double, longitude: Double): Result<List<ShortLocation>>
}
