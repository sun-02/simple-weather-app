package com.example.simpleweatherapp.data.openweather

import com.example.simpleweatherapp.model.Result
import com.example.simpleweatherapp.model.bingmaps.BingMapsResponse
import com.example.simpleweatherapp.model.openweather.OpenWeatherResponse
import com.example.simpleweatherapp.model.openweather.UnitSystem
import com.example.simpleweatherapp.util.DataUtils
import retrofit2.Retrofit

class RemoteWeatherDataSource(retrofit: Retrofit) {

    private val service = retrofit.create(OpenWeatherService::class.java)

    suspend fun getOpenWeather(
        latitude: Double,
        longitude: Double,
        unitSystem: UnitSystem
    ): Result<OpenWeatherResponse> =
        DataUtils.getResponse { service.fetchOpenWeather(latitude, longitude, unitSystem) }
}