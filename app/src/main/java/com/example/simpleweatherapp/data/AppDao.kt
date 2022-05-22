package com.example.simpleweatherapp.data

import androidx.room.*
import com.example.simpleweatherapp.model.bingmaps.ShortLocation
import com.example.simpleweatherapp.model.openweather.OneCallWeather

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: OneCallWeather)

    @Delete
    suspend fun deleteWeatherList(weatherList: List<OneCallWeather>)

    @Query("DELETE FROM one_call_weather WHERE date_time < :latestDateEpochSeconds")
    suspend fun deleteOldWeather(latestDateEpochSeconds: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShortLocation(sLocation: ShortLocation)

    @Delete
    suspend fun deleteShortLocation(sLocation: ShortLocation)
}