package com.example.simpleweatherapp.data.openweather

import com.example.simpleweatherapp.model.Result
import com.example.simpleweatherapp.model.bingmaps.ShortLocation
import com.example.simpleweatherapp.model.openweather.OneCallWeather
import com.example.simpleweatherapp.model.openweather.UnitSystem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import java.time.LocalDateTime

class DefaultWeatherRepository(
    private val remoteWeatherDataSource: RemoteWeatherDataSource,
    private val localWeatherDataSource: LocalWeatherDataSource,
) : WeatherRepository {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getOneCallWeather(
        sLocation: ShortLocation,
        unitSystem: UnitSystem
    ): Result<OneCallWeather> {
        val weatherResult = getRemoteOneCallWeather(sLocation, unitSystem)
        when (weatherResult) {
            is Result.Success -> {
                Timber.d("Got weather=${weatherResult.data} from remote source")
                weatherResult.data.name = sLocation.name
                Timber.d("Name=${sLocation.name} added to weather")
                saveOneCallWeather(weatherResult.data)
                return weatherResult
            }
            is Result.Error -> {
                Timber.d("Weather remote source returned error=$weatherResult")
                return getLocalOneCallWeather(sLocation.name)
            }
        }


        return weatherResult
    }

    private suspend fun getRemoteOneCallWeather(
        sLocation: ShortLocation,
         unitSystem: UnitSystem
    ): Result<OneCallWeather> = remoteWeatherDataSource.fetchOneCallWeather(
            sLocation.latitude,
            sLocation.longitude,
            unitSystem
        )

    private suspend fun getLocalOneCallWeather(name: String): Result<OneCallWeather> {

    }

    private suspend fun saveOneCallWeather(weather: OneCallWeather) {}

    private suspend fun deleteOneCallWeather(weather: OneCallWeather) {}

    private suspend fun deleteOldOneCallWeather(weather: OneCallWeather) {}


}