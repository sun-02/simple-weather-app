package com.example.simpleweatherapp.model.openweather

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.simpleweatherapp.model.bingmaps.ShortLocation
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

@Entity(tableName = "one_call_weather")
data class OneCallWeather(
    @PrimaryKey  @ColumnInfo(name = "date_time") val dateTime: LocalDateTime,
    val temp: Double,
    @ColumnInfo(name = "feels_like") val feelsLike: Double,
    val pressure: Int,
    val humidity: Int,
    @ColumnInfo(name = "dew_point") val dewPoint: Double,
    val uvi: Double,
    val visibility: Int,
    @ColumnInfo(name = "wind_speed") val windSpeed: Double,
    @ColumnInfo(name = "wind_deg") val windDeg: Int,
    @ColumnInfo(name = "weather_title") val weatherTitle: String,
    @ColumnInfo(name = "weather_icon") val weatherIcon: String,
    @ColumnInfo(name = "hourly_forecast") val hourlyForecast: List<HourlyForecast>,
    @ColumnInfo(name = "daily_forecast") val dailyForecast: List<DailyForecast>
) {
    var name: String? = null
    set(value) {
        if (field == null) {
            field = value
        } else {
            throw IllegalArgumentException("This is single-use setter. Value already has been set.")
        }
    }
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
    @ColumnInfo(name = "parent_date_time") val parentDateTime: LocalDateTime,
    @ColumnInfo(name = "date_epoch") val dateEpoch: Long,
    val time: LocalTime,
    val temp: Double,
    @ColumnInfo(name = "weather_icon") val weatherIcon: String
)

@Entity(tableName = "hourly_forecast", foreignKeys = arrayOf(
    ForeignKey(
        entity = OneCallWeather::class,
        parentColumns = arrayOf("date_time"),
        childColumns = arrayOf("parent_date_time"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )))
data class DailyForecast(
    @ColumnInfo(name = "parent_date_time") val parentDateTime: LocalDateTime,
    val date: LocalDate,
    @ColumnInfo(name = "morn_temp") val mornTemp: Double,
    @ColumnInfo(name = "day_temp") val dayTemp: Double,
    @ColumnInfo(name = "eve_temp") val eveTemp: Double,
    @ColumnInfo(name = "night_temp") val nightTemp: Double,
    @ColumnInfo(name = "morn_feels_like") val mornFeelsLike: Double,
    @ColumnInfo(name = "day_feels_like") val dayFeelsLike: Double,
    @ColumnInfo(name = "eve_feels_like") val eveFeelsLike: Double,
    @ColumnInfo(name = "night_feels_like") val nightFeelsLike: Double,
    val pressure: Int,
    val humidity: Int,
    val uvi: Double,
    @ColumnInfo(name = "wind_speed") val windSpeed: Double,
    @ColumnInfo(name = "wind_deg") val windDeg: Int,
    val sunrise: LocalTime,
    val sunset: LocalTime,
    val moonrise: LocalTime,
    val moonset: LocalTime,
    @ColumnInfo(name = "weather_icon") val weatherIcon: String
)