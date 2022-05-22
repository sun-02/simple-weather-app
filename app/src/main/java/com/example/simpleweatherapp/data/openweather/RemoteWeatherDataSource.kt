package com.example.simpleweatherapp.data.openweather

import com.example.simpleweatherapp.model.Result
import com.example.simpleweatherapp.model.openweather.OneCallWeather
import com.example.simpleweatherapp.model.openweather.UnitSystem
import com.example.simpleweatherapp.util.DataUtils
import retrofit2.Retrofit

class RemoteWeatherDataSource(retrofit: Retrofit) {

    private val service = retrofit.create(OpenWeatherService::class.java)

    suspend fun fetchOneCallWeather(
        latitude: Double,
        longitude: Double,
        unitSystem: UnitSystem
    ): Result<OneCallWeather> =
        DataUtils.getResponse { service.fetchOneCallWeather(latitude, longitude, unitSystem) }
}