package com.example.simpleweatherapp.data.openweather

import com.example.simpleweatherapp.data.LocalDataSource
import com.example.simpleweatherapp.model.Result
import com.example.simpleweatherapp.model.bingmaps.ShortLocation
import com.example.simpleweatherapp.model.openweather.OneCallWeather
import com.example.simpleweatherapp.model.openweather.OpenWeatherResponse
import com.example.simpleweatherapp.model.openweather.ShortWeather
import com.example.simpleweatherapp.model.openweather.UnitSystem
import com.example.simpleweatherapp.util.DataUtils
import timber.log.Timber

class DefaultWeatherRepository(
    private val remoteWeatherDataSource: RemoteWeatherDataSource,
    private val localDataSource: LocalDataSource
) : WeatherRepository {

    override suspend fun getWeather(
        sLocation: ShortLocation,
        unitSystem: UnitSystem
    ): Result<OneCallWeather> {
        val weatherResult = getRemoteWeather(sLocation, unitSystem)
        return if (weatherResult is Result.Success) {
            Timber.d("Got weather=$weatherResult from remote source")
            val weather = DataUtils.getOneCallWeather(sLocation, weatherResult.data)
            saveWeather(weather)
            Timber.d("Weather $weather saved to database")
            Result.Success(weather)
        } else {
            Timber.d("Weather remote source returned error=$weatherResult")
            getLocalWeather(sLocation.name)
        }
    }

    private suspend fun getRemoteWeather(
        sLocation: ShortLocation,
         unitSystem: UnitSystem
    ): Result<OpenWeatherResponse> = remoteWeatherDataSource.getOpenWeather(
            sLocation.latitude,
            sLocation.longitude,
            unitSystem
        )

    override suspend fun getFavWeatherList(
        sLocationList: List<ShortLocation>
    ): Result<List<ShortWeather>> = localDataSource.getFavWeatherList(sLocationList)

    private suspend fun getLocalWeather(name: String): Result<OneCallWeather> =
        localDataSource.getWeather(name)

    private suspend fun saveWeather(weather: OneCallWeather) =
        localDataSource.saveWeather(weather)

    override suspend fun deleteOldWeather(latestDateEpochSeconds: Int) =
        localDataSource.deleteOldWeather(latestDateEpochSeconds)


}