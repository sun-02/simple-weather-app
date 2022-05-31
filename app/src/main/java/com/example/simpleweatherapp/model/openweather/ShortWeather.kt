package com.example.simpleweatherapp.model.openweather

import androidx.room.ColumnInfo

data class ShortWeather(
    val name: String,
    val temp: Int,
    @ColumnInfo(name = "weather_icon") val weatherIcon: String
)