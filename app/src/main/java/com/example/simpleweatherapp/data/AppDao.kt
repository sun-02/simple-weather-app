package com.example.simpleweatherapp.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.simpleweatherapp.model.bingmaps.ShortLocation
import com.example.simpleweatherapp.model.openweather.DailyForecast
import com.example.simpleweatherapp.model.openweather.HourlyForecast
import com.example.simpleweatherapp.model.openweather.OneCallWeather
import com.example.simpleweatherapp.model.openweather.ShortWeather

@Dao
@TypeConverters(Converters::class)
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOneCallWeather(weather: OneCallWeather)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHourlyForecastList(hourlyForecastList: List<HourlyForecast>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyForecastList(dailyForecastList: List<DailyForecast>)

    @Query("SELECT * FROM hourly_forecast WHERE parent_name = :parentName")
    suspend fun getHourlyForecastList(parentName: String): List<HourlyForecast>?

    @Query("SELECT * FROM daily_forecast WHERE parent_name = :parentName")
    suspend fun getDailyForecastList(parentName: String): List<DailyForecast>?

    @Query("SELECT * FROM one_call_weather WHERE name = :name")
    suspend fun getOneCallWeather(name: String): OneCallWeather?

    @Query("SELECT `name`, `temp`, weather_icon FROM one_call_weather WHERE name IN (:names)")
    fun getFavWeatherList(names: List<String>): List<ShortWeather>?

    @Query("DELETE FROM one_call_weather WHERE instant < :thresholdEpochSeconds")
    suspend fun deleteOldWeather(thresholdEpochSeconds: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavLocation(sLocation: ShortLocation)

    @Query("SELECT * FROM short_location")
    fun observeFavLocationList(): LiveData<List<ShortLocation>?>

    @Delete
    suspend fun deleteShortLocation(sLocation: ShortLocation)

    @Query("SELECT 1 FROM one_call_weather WHERE name = :name")
    suspend fun isWeatherSaved(name: String): Int?
}