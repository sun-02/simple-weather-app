package com.example.simpleweatherapp.util

import com.example.simpleweatherapp.model.Result
import com.example.simpleweatherapp.model.bingmaps.ShortLocation
import com.example.simpleweatherapp.model.openweather.DailyForecast
import com.example.simpleweatherapp.model.openweather.HourlyForecast
import com.example.simpleweatherapp.model.openweather.OneCallWeather
import com.example.simpleweatherapp.model.openweather.OpenWeatherResponse
import retrofit2.Response
import timber.log.Timber
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

    fun getOneCallWeather(sLocation: ShortLocation, wResponse: OpenWeatherResponse): OneCallWeather {
        val name = sLocation.name
        val offset = wResponse.timezone_offset
        val zoneOffset = ZoneOffset.ofTotalSeconds(offset)
        val hourlyForecast = mutableListOf<HourlyForecast>()
        val currentDateTime = LocalDateTime.ofEpochSecond(wResponse.current.dt.toLong(), 0,
            zoneOffset)
        for (hw in wResponse.hourly) {
            hourlyForecast.add(
                HourlyForecast(
                parentName = name,
                dateTime = LocalDateTime.ofEpochSecond(hw.dt.toLong(), 0, zoneOffset),
                temp = hw.temp.toInt(),
                weatherIcon = hw.weather[0].icon
            )
            )
        }
        val dailyForecast = mutableListOf<DailyForecast>()
        for (dw in wResponse.daily) {
            dailyForecast.add(
                DailyForecast(
                    parentName = name,
                    date = LocalDate.ofEpochDay((offset + dw.dt) / SECONDS_IN_DAY),
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
                    sunrise = getSafeLocalTime(offset + dw.sunrise),
                    sunset = getSafeLocalTime(offset + dw.sunset),
                    moonrise = getSafeLocalTime(offset + dw.moonrise),
                    moonset = getSafeLocalTime(offset + dw.moonset),
                    weatherIcon = dw.weather[0].icon
                )
            )
        }
        val ocw = OneCallWeather(
            name = name,
            dateTime = currentDateTime,
            temp = wResponse.current.temp.toInt(),
            feelsLike = wResponse.current.feels_like.toInt(),
            pressure = wResponse.current.pressure,
            humidity = wResponse.current.humidity,
            dewPoint = wResponse.current.dew_point.toInt(),
            uvi = wResponse.current.uvi.toInt(),
            visibility = wResponse.current.visibility,
            windSpeed = wResponse.current.wind_speed.toInt(),
            windDeg = wResponse.current.wind_deg,
            weatherTitle = wResponse.current.weather[0].main,
            weatherIcon = wResponse.current.weather[0].icon,
        )
        ocw.let {
            it.hourlyForecast = hourlyForecast
            it.dailyForecast = dailyForecast
        }
        return ocw
    }
}