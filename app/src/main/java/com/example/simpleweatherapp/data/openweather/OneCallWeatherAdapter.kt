package com.example.simpleweatherapp.data.openweather

import com.example.simpleweatherapp.model.openweather.*
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

class OneCallWeatherAdapter {

    companion object {
        private const val secondsInDay = 86400L
    }

    @FromJson
    fun fromOpenWeatherResponse(r: OpenWeatherResponse): OneCallWeather {
        val offset = r.timezone_offset
        val hourlyForecast = mutableListOf<HourlyForecast>()
        val currentDateTime = LocalDateTime.ofEpochSecond(r.current.dt.toLong(), 0,
            ZoneOffset.ofTotalSeconds(offset))
        for (hw in r.hourly) {
            hourlyForecast.add(HourlyForecast(
                parentDateTime = currentDateTime,
                dateEpoch = hw.dt.toLong(),
                time = LocalTime.ofSecondOfDay((offset + hw.dt) % secondsInDay),
                temp = hw.temp.toInt(),
                weatherIcon = hw.weather[0].icon
                ))
        }
        val dailyForecast = mutableListOf<DailyForecast>()
        for (dw in r.daily) {
            dailyForecast.add(
                DailyForecast(
                    parentDateTime = currentDateTime,
                    date = LocalDate.ofEpochDay((offset + dw.dt) / secondsInDay),
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
                    sunrise = LocalTime.ofSecondOfDay((offset + dw.sunrise) % secondsInDay),
                    sunset = LocalTime.ofSecondOfDay((offset + dw.sunset) % secondsInDay),
                    moonrise = LocalTime.ofSecondOfDay((offset + dw.moonrise) % secondsInDay),
                    moonset = LocalTime.ofSecondOfDay((offset + dw.moonset) % secondsInDay),
                    weatherIcon = dw.weather[0].icon
                )
            )
        }
        val ocw = OneCallWeather(
            dateTime = currentDateTime,
            temp = r.current.temp.toInt(),
            feelsLike = r.current.feels_like.toInt(),
            pressure = r.current.pressure,
            humidity = r.current.humidity,
            dewPoint = r.current.dew_point.toInt(),
            uvi = r.current.uvi.toInt(),
            visibility = r.current.visibility,
            windSpeed = r.current.wind_speed.toInt(),
            windDeg = r.current.wind_deg,
            weatherTitle = r.current.weather[0].main,
            weatherIcon = r.current.weather[0].icon,
        )
        ocw.let {
            it.hourlyForecast = hourlyForecast
            it.dailyForecast = dailyForecast
        }
        return ocw
    }

    @ToJson
    fun stub(w: OneCallWeather): OpenWeatherResponse =
        OpenWeatherResponse(
            0.0,
            0.0,
            "",
            0,
            Current(
                0,
                0,
                0,
                0.0,
                0.0,
                0,
                0,
                0.0,
                0.0,
                0,
                0,
                0.0,
                0,
                null,
                listOf(),
                null,
                null
            ),
            listOf(),
            listOf()
        )
}
