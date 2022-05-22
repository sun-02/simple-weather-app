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
                temp = hw.temp,
                weatherIcon = hw.weather[0].icon
                ))
        }
        val dailyForecast = mutableListOf<DailyForecast>()
        for (dw in r.daily) {
            dailyForecast.add(
                DailyForecast(
                    parentDateTime = currentDateTime,
                    date = LocalDate.ofEpochDay((offset + dw.dt) / secondsInDay),
                    mornTemp = dw.temp.morn,
                    dayTemp = dw.temp.day,
                    eveTemp = dw.temp.eve,
                    nightTemp = dw.temp.night,
                    mornFeelsLike = dw.feels_like.morn,
                    dayFeelsLike = dw.feels_like.day,
                    eveFeelsLike = dw.feels_like.eve,
                    nightFeelsLike = dw.feels_like.night,
                    pressure = dw.pressure,
                    humidity = dw.humidity,
                    uvi = dw.uvi,
                    windSpeed = dw.wind_speed,
                    windDeg = dw.wind_deg,
                    sunrise = LocalTime.ofSecondOfDay((offset + dw.sunrise) % secondsInDay),
                    sunset = LocalTime.ofSecondOfDay((offset + dw.sunset) % secondsInDay),
                    moonrise = LocalTime.ofSecondOfDay((offset + dw.moonrise) % secondsInDay),
                    moonset = LocalTime.ofSecondOfDay((offset + dw.moonset) % secondsInDay),
                    weatherIcon = dw.weather[0].icon
                )
            )
        }
        return OneCallWeather(
            dateTime = currentDateTime,
            temp = r.current.temp,
            feelsLike = r.current.feels_like,
            pressure = r.current.pressure,
            humidity = r.current.humidity,
            dewPoint = r.current.dew_point,
            uvi = r.current.uvi,
            visibility = r.current.visibility,
            windSpeed = r.current.wind_speed,
            windDeg = r.current.wind_deg,
            weatherTitle = r.current.weather[0].main,
            weatherIcon = r.current.weather[0].icon,
            hourlyForecast = hourlyForecast,
            dailyForecast = dailyForecast
        )
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
