package com.example.simpleweatherapp.model.openweather

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OpenWeatherResponse(
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Int,
    val current: Current,
    val daily: List<Daily>,
    val hourly: List<Hourly>
)

@JsonClass(generateAdapter = true)
data class Current(
    val dt: Int,
    val sunrise: Int,
    val sunset: Int,
    val temp: Double,
    val feels_like: Double,
    val pressure: Int,
    val humidity: Int,
    val dew_point: Double,
    val uvi: Double,
    val clouds: Int,
    val visibility: Int,
    val wind_speed: Double,
    val wind_deg: Int,
    val wind_gust: Double?,
    val weather: List<Weather>,
    val rain: Rain?,
    val snow: Snow?
)

@JsonClass(generateAdapter = true)
data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

@JsonClass(generateAdapter = true)
data class Rain(
    @Json(name = "1h") val oneHourPrecipitation: Double
)

@JsonClass(generateAdapter = true)
data class Snow(
    @Json(name = "1h") val oneHourPrecipitation: Double
)

@JsonClass(generateAdapter = true)
data class Hourly(
    val dt: Int,
    val temp: Double,
    val feels_like: Double,
    val pressure: Int,
    val humidity: Int,
    val dew_point: Double,
    val uvi: Double,
    val clouds: Int,
    val visibility: Int,
    val wind_speed: Double,
    val wind_deg: Int,
    val wind_gust: Double?,
    val weather: List<Weather>,
    val pop: Double,
    val rain: Rain?,
    val snow: Snow?
)

@JsonClass(generateAdapter = true)
data class Daily(
    val dt: Int,
    val sunrise: Int,
    val sunset: Int,
    val moonrise: Int,
    val moonset: Int,
    val moon_phase: Double,
    val temp: Temp,
    val feels_like: FeelsLike,
    val pressure: Int,
    val humidity: Int,
    val dew_point: Double,
    val wind_speed: Double,
    val wind_deg: Int,
    val wind_gust: Double?,
    val weather: List<Weather>,
    val clouds: Int,
    val pop: Double,
    val uvi: Double,
    val rain: Double?,
    val snow: Double?
)

@JsonClass(generateAdapter = true)
data class Temp(
    val day: Double,
    val min: Double,
    val max: Double,
    val night: Double,
    val eve: Double,
    val morn: Double
)

@JsonClass(generateAdapter = true)
data class FeelsLike(
    val day: Double,
    val night: Double,
    val eve: Double,
    val morn: Double
)
