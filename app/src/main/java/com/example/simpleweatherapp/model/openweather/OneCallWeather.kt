package com.example.simpleweatherapp.model.openweather

import androidx.room.*
import java.time.*

@Entity(tableName = "one_call_weather")
data class OneCallWeather(
    @PrimaryKey  var name: String,
    val instant: Instant,
    @ColumnInfo(name = "zone_offset") val zoneOffset: ZoneOffset,
    val temp: Int,
    @ColumnInfo(name = "feels_like") val feelsLike: Int,
    val pressure: Int,
    val humidity: Int,
    @ColumnInfo(name = "dew_point") val dewPoint: Int,
    val uvi: Int,
    val visibility: Int,
    @ColumnInfo(name = "wind_speed") val windSpeed: Int,
    @ColumnInfo(name = "wind_deg") val windDeg: Int,
    @ColumnInfo(name = "weather_title") val weatherTitle: String,
    @ColumnInfo(name = "weather_icon") val weatherIcon: String,
) {
    @Ignore var hourlyForecast: List<HourlyForecast>? = null
    @Ignore var dailyForecast: List<DailyForecast>? = null
}

@Entity(tableName = "hourly_forecast", foreignKeys = [ForeignKey(
    entity = OneCallWeather::class,
    parentColumns = arrayOf("name"),
    childColumns = arrayOf("parent_name"),
    onDelete = ForeignKey.CASCADE,
    onUpdate = ForeignKey.CASCADE
)], primaryKeys = ["parent_name", "instant"])
data class HourlyForecast(
    @ColumnInfo(name = "parent_name") val parentName: String,
    val instant: Instant,
    @ColumnInfo(name = "zone_offset") val zoneOffset: ZoneOffset,
    val temp: Int,
    @ColumnInfo(name = "weather_icon") val weatherIcon: String
)

@Entity(tableName = "daily_forecast", foreignKeys = [ForeignKey(
    entity = OneCallWeather::class,
    parentColumns = arrayOf("name"),
    childColumns = arrayOf("parent_name"),
    onDelete = ForeignKey.CASCADE,
    onUpdate = ForeignKey.CASCADE
)], primaryKeys = ["parent_name", "instant"])
data class DailyForecast(
    @ColumnInfo(name = "parent_name") val parentName: String,
    val instant: Instant,
    @ColumnInfo(name = "zone_offset") val zoneOffset: ZoneOffset,
    @ColumnInfo(name = "morn_temp") val mornTemp: Int,
    @ColumnInfo(name = "day_temp") val dayTemp: Int,
    @ColumnInfo(name = "eve_temp") val eveTemp: Int,
    @ColumnInfo(name = "night_temp") val nightTemp: Int,
    @ColumnInfo(name = "morn_feels_like") val mornFeelsLike: Int,
    @ColumnInfo(name = "day_feels_like") val dayFeelsLike: Int,
    @ColumnInfo(name = "eve_feels_like") val eveFeelsLike: Int,
    @ColumnInfo(name = "night_feels_like") val nightFeelsLike: Int,
    val pressure: Int,
    val humidity: Int,
    val uvi: Int,
    @ColumnInfo(name = "wind_speed") val windSpeed: Int,
    @ColumnInfo(name = "wind_deg") val windDeg: Int,
    val sunrise: Instant,
    val sunset: Instant,
    val moonrise: Instant,
    val moonset: Instant,
    @ColumnInfo(name = "weather_icon") val weatherIcon: String
)