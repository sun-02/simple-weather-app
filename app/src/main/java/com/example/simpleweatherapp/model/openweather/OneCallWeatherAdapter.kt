package com.example.simpleweatherapp.model.openweather;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson
import java.time.LocalDate
import java.time.LocalTime

class OneCallWeatherAdapter {

    private val secondsInDay = 86400L

    @FromJson
    fun fromOpenWeatherResponse(r: OpenWeatherResponse): OneCallWeather {
        val offset = r.timezone_offset
        val hourlyForecast = mutableListOf<HourlyForecast>()
        for (hw in r.hourly) {
            hourlyForecast.add(
                HourlyForecast(
                    dateEpoch = hw.dt.toLong(),
                    time = LocalTime.ofSecondOfDay((offset + hw.dt) % secondsInDay),
                    temp = hw.temp,
                    weatherIcon = hw.weather[0].icon
                )
            )
        }
        val dailyForecast = mutableListOf<DailyForecast>()
        for (dw in r.daily) {
            dailyForecast.add(
                DailyForecast(
                    date = LocalDate.ofEpochDay((offset + dw.dt) / secondsInDay),
                    dayTemp = dw.temp.day,
                    nightTemp = dw.temp.night,
                    pressure = dw.pressure,
                    humidity = dw.humidity,
                    uvi = dw.uvi,
                    windSpeed = dw.wind_speed,
                    windDeg = dw.wind_deg,
                    weatherIcon = dw.weather[0].icon
                )
            )
        }
        return OneCallWeather(
            date = LocalDate.ofEpochDay((r.current.dt + offset) / secondsInDay),
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
