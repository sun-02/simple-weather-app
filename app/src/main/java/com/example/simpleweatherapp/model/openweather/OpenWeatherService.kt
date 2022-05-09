package com.example.simpleweatherapp.model.openweather

import com.example.simpleweatherapp.Config
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherService {
    @GET(Config.OPEN_WEATHER_ONE_CALL_API_URL)
    suspend fun fetchOneCallWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") unitSystem: UnitSystem,
        @Query("appid") appId: String,
        @Query("exclude") exclude: String = "minutely,alerts"
    ): Response<OneCallWeather>
}