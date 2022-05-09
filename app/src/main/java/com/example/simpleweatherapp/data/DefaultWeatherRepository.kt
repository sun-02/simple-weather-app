package com.example.simpleweatherapp.data

import com.example.simpleweatherapp.model.Result
import com.example.simpleweatherapp.model.openweather.OneCallWeather
import com.example.simpleweatherapp.model.openweather.UnitSystem

class DefaultWeatherRepository(
    private val remoteWeatherDataSource: RemoteWeatherDataSource
) : WeatherRepository {

    override suspend fun getOneCallWeather(
        latitude: Double,
        longitude: Double,
        unitSystem: UnitSystem
    ): Result<OneCallWeather> =
        remoteWeatherDataSource.fetchOneCallWeather(
            latitude,
            longitude,
            unitSystem
        )
}