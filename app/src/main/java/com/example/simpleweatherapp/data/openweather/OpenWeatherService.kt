package com.example.simpleweatherapp.data.openweather

import com.example.simpleweatherapp.BuildConfig
import com.example.simpleweatherapp.Config
import com.example.simpleweatherapp.model.openweather.OneCallWeather
import com.example.simpleweatherapp.model.openweather.UnitSystem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherService {
    @GET(Config.OPEN_WEATHER_ONE_CALL_API_URL)
    suspend fun fetchOneCallWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") unitSystem: UnitSystem,
        @Query("exclude") exclude: String = "minutely,alerts",
        @Query("appid") appId: String = BuildConfig.OPENWEATHER_KEY
    ): Response<OneCallWeather>
}