package com.example.simpleweatherapp.data.bingmaps

import com.example.simpleweatherapp.model.bingmaps.ShortLocation
import com.example.simpleweatherapp.model.Result

interface MapsRepository {
    suspend fun getLocationsList(name: String): Result<List<ShortLocation>>
    suspend fun getLocation(latitude: Double, longitude: Double): Result<List<ShortLocation>>
}
