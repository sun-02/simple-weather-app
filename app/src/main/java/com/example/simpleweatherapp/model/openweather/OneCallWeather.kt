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
    val mornTemp: Double,
    val dayTemp: Double,
    val eveTemp: Double,
    val nightTemp: Double,
    val mornFeelsLike: Double,
    val dayFeelsLike: Double,
    val eveFeelsLike: Double,
    val nightFeelsLike: Double,
    val pressure: Int,
    val humidity: Int,
    val uvi: Double,
    val windSpeed: Double,
    val windDeg: Int,
    val sunrise: LocalTime,
    val sunset: LocalTime,
    val moonrise: LocalTime,
    val moonset: LocalTime,
    val weatherIcon: String
)