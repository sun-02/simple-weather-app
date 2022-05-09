package com.example.simpleweatherapp.model.openweather

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

data class OneCallWeather(
    val date: LocalDate,
    val temp: Double,
    val feelsLike: Double,
    val pressure: Int,
    val humidity: Int,
    val dewPoint: Double,
    val uvi: Double,
    val visibility: Int,
    val windSpeed: Double,
    val windDeg: Int,
    val weatherTitle: String,
    val weatherIcon: String,
    val hourlyForecast: List<HourlyForecast>,
    val dailyForecast: List<DailyForecast>
)

data class HourlyForecast(
    val dateEpoch: Long,
    val time: LocalTime,
    val temp: Double,
    val weatherIcon: String
)

data class DailyForecast(
    val date: LocalDate,
    val dayTemp: Double,
    val nightTemp: Double,
    val pressure: Int,
    val humidity: Int,
    val uvi: Double,
    val windSpeed: Double,
    val windDeg: Int,
    val weatherIcon: String,
    var expanded: Boolean = false
)