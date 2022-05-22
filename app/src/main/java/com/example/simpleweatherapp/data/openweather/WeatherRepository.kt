package com.example.simpleweatherapp.data.openweather

import com.example.simpleweatherapp.model.Result
import com.example.simpleweatherapp.model.bingmaps.ShortLocation
import com.example.simpleweatherapp.model.openweather.OneCallWeather
import com.example.simpleweatherapp.model.openweather.UnitSystem

interface WeatherRepository {

    suspend fun getOneCallWeather(sLocation: ShortLocation,
                                  unitSystem: UnitSystem): Result<OneCallWeather>
}

