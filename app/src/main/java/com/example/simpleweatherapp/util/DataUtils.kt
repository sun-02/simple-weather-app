package com.example.simpleweatherapp.util

import com.example.simpleweatherapp.model.Result
import com.example.simpleweatherapp.model.bingmaps.ShortLocation
import com.example.simpleweatherapp.model.openweather.DailyForecast
import com.example.simpleweatherapp.model.openweather.HourlyForecast
import com.example.simpleweatherapp.model.openweather.OneCallWeather
import com.example.simpleweatherapp.model.openweather.OpenWeatherResponse
import retrofit2.Response
import timber.log.Timber
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

object DataUtils {

    suspend fun <T: Any> getResponse(
        request: suspend () -> Response<T>
    ): Result<T> {
        Timber.d("Requesting $request")
        return try {
            val result = request.invoke()
            Timber.d("Got $result")
            val body = result.body()
            if (result.isSuccessful && body != null) {
                Result.Success(body)
            } else {
                Result.Error(result.code(), result.message())
            }
        } catch (e: Throwable) {
            Result.Error(-1, e.toString())
        }
    }

    fun getOneCallWeather(sLocation: ShortLocation, wResponse: OpenWeatherResponse): OneCallWeather =
        with(wResponse) {
            val name = sLocation.name
            val zoneOffset = ZoneOffset.ofTotalSeconds(timezone_offset)
            val hourlyForecast = mutableListOf<HourlyForecast>()
            for (hw in wResponse.hourly) {
                hourlyForecast.add(
                    HourlyForecast(
                        parentName = name,
                        instant = Instant.ofEpochSecond(hw.dt),
                        zoneOffset = zoneOffset,
                        temp = hw.temp.toInt(),
                        weatherIcon = hw.weather[0].icon
                    )
                )
            }
            val dailyForecast = mutableListOf<DailyForecast>()
            for (dw in daily) {
                dailyForecast.add(
                    DailyForecast(
                        parentName = name,
                        instant = Instant.ofEpochSecond(dw.dt),
                        zoneOffset = zoneOffset,
                        mornTemp = dw.temp.morn.toInt(),
                        dayTemp = dw.temp.day.toInt(),
                        eveTemp = dw.temp.eve.toInt(),
                        nightTemp = dw.temp.night.toInt(),
                        mornFeelsLike = dw.feels_like.morn.toInt(),
                        dayFeelsLike = dw.feels_like.day.toInt(),
                        eveFeelsLike = dw.feels_like.eve.toInt(),
                        nightFeelsLike = dw.feels_like.night.toInt(),
                        pressure = dw.pressure,
                        humidity = dw.humidity,
                        uvi = dw.uvi.toInt(),
                        windSpeed = dw.wind_speed.toInt(),
                        windDeg = dw.wind_deg,
                        sunrise = Instant.ofEpochSecond(dw.sunrise),
                        sunset = Instant.ofEpochSecond(dw.sunset),
                        moonrise = Instant.ofEpochSecond(dw.moonrise),
                        moonset = Instant.ofEpochSecond(dw.moonset),
                        weatherIcon = dw.weather[0].icon
                    )
                )
            }
            with(current) {
                val ocw = OneCallWeather(
                    name = name,
                    instant = Instant.ofEpochSecond(dt),
                    zoneOffset = ZoneOffset.ofTotalSeconds(timezone_offset),
                    temp = temp.toInt(),
                    feelsLike = feels_like.toInt(),
                    pressure = pressure,
                    humidity = humidity,
                    dewPoint = dew_point.toInt(),
                    uvi = uvi.toInt(),
                    visibility = visibility,
                    windSpeed = wind_speed.toInt(),
                    windDeg = wind_deg,
                    weatherTitle = weather[0].main,
                    weatherIcon = weather[0].icon,
                )
                ocw.let {
                    it.hourlyForecast = hourlyForecast
                    it.dailyForecast = dailyForecast
                }
                ocw
            }
        }
}