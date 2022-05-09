package com.example.simpleweatherapp.data

import com.example.simpleweatherapp.BuildConfig
import com.example.simpleweatherapp.model.Result
import com.example.simpleweatherapp.model.openweather.OneCallWeather
import com.example.simpleweatherapp.model.openweather.OpenWeatherService
import com.example.simpleweatherapp.model.openweather.UnitSystem
import com.example.simpleweatherapp.util.DataUtils
import retrofit2.Retrofit

class RemoteWeatherDataSource(
    private val retrofit: Retrofit
) {
    private val service = retrofit.create(OpenWeatherService::class.java)

    suspend fun fetchOneCallWeather(
        latitude: Double,
        longitude: Double,
        unitSystem: UnitSystem
    ): Result<OneCallWeather> = DataUtils.getResponse(
            { service.fetchOneCallWeather(
                latitude,
                longitude,
                unitSystem,
                BuildConfig.OPENWEATHER_KEY) },
            "Failed to fetch One Call API weather",
            "One Call API weather fetch caused an exception",
            retrofit
        )
}