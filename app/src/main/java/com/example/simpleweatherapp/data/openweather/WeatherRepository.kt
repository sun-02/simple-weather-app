package com.example.simpleweatherapp.data.openweather

import androidx.lifecycle.LiveData
import com.example.simpleweatherapp.model.Result
import com.example.simpleweatherapp.model.bingmaps.ShortLocation
import com.example.simpleweatherapp.model.openweather.OneCallWeather
import com.example.simpleweatherapp.model.openweather.ShortWeather
import com.example.simpleweatherapp.model.openweather.UnitSystem

interface WeatherRepository {

    suspend fun getWeather(sLocation: ShortLocation,
                           unitSystem: UnitSystem): Result<OneCallWeather>

    suspend fun getFavWeatherList(
        sLocationList: List<ShortLocation>
    ): Result<List<ShortWeather>>

    suspend fun deleteOldWeather(latestDateEpochSeconds: Int)
}

