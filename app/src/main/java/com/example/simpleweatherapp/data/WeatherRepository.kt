package com.example.simpleweatherapp.data

import com.example.simpleweatherapp.model.Result
import com.example.simpleweatherapp.model.openweather.OneCallWeather
import com.example.simpleweatherapp.model.openweather.UnitSystem

interface WeatherRepository {

    suspend fun getOneCallWeather(
        latitude: Double,
        longitude: Double,
        unitSystem: UnitSystem
    ): Result<OneCallWeather>
}

