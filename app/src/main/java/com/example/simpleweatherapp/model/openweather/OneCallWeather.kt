package com.example.simpleweatherapp.model.openweather

import androidx.room.*
import com.example.simpleweatherapp.data.Converters
import com.example.simpleweatherapp.model.bingmaps.ShortLocation
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

@Entity(tableName = "one_call_weather")
data class OneCallWeather(
    @PrimaryKey  @ColumnInfo(name = "date_time") val dateTime: LocalDateTime,
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
    var name: String? = null
) {
//    constructor() : this(LocalDateTime.now(), 0, 0, 0, 0, 0, 0, 0, 0, 0, "", "")
    @Ignore var hourlyForecast: List<HourlyForecast>? = null
    @Ignore var dailyForecast: List<DailyForecast>? = null
}

@Entity(tableName = "hourly_forecast", foreignKeys = arrayOf(
    ForeignKey(
        entity = OneCallWeather::class,
        parentColumns = arrayOf("date_time"),
        childColumns = arrayOf("parent_date_time"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )))
data class HourlyForecast(
    @PrimaryKey  @ColumnInfo(name = "parent_date_time") val parentDateTime: LocalDateTime,
    @ColumnInfo(name = "date_epoch") val dateEpoch: Long,
    val time: LocalTime,
    val temp: Int,
    @ColumnInfo(name = "weather_icon") val weatherIcon: String
)

@Entity(tableName = "daily_forecast", foreignKeys = arrayOf(
    ForeignKey(
        entity = OneCallWeather::class,
        parentColumns = arrayOf("date_time"),
        childColumns = arrayOf("parent_date_time"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )))
data class DailyForecast(
    @PrimaryKey  @ColumnInfo(name = "parent_date_time") val parentDateTime: LocalDateTime,
    val date: LocalDate,
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
    val sunrise: LocalTime,
    val sunset: LocalTime,
    val moonrise: LocalTime,
    val moonset: LocalTime,
    @ColumnInfo(name = "weather_icon") val weatherIcon: String
)