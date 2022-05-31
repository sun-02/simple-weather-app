package com.example.simpleweatherapp.data

import com.example.simpleweatherapp.model.openweather.OneCallWeather
import com.example.simpleweatherapp.model.Result
import com.example.simpleweatherapp.model.bingmaps.ShortLocation
import com.example.simpleweatherapp.model.openweather.ShortWeather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.ZoneOffset

class LocalDataSource(private val appDao: AppDao) {

    suspend fun saveWeather(weather: OneCallWeather) =
        withContext(Dispatchers.IO) {
            appDao.insertOneCallWeather(weather)
            appDao.insertHourlyForecastList(weather.hourlyForecast!!)
            appDao.insertDailyForecastList(weather.dailyForecast!!)
        }

    suspend fun getWeather(name: String): Result<OneCallWeather> =
        withContext(Dispatchers.IO) {
            try {
                val oneCallWeather = appDao.getOneCallWeather(name)
                if (oneCallWeather == null) {
                    Timber.d("Weather not found in local source")
                    return@withContext Result.Error( 2,
                        "Weather not found in local source")
                }

                val zeroOffset = ZoneOffset.ofTotalSeconds(0)
                val weatherDateEpochSeconds = oneCallWeather.dateTime
                    .toEpochSecond(zeroOffset).toInt()
                val hourlyForecast = appDao.getHourlyForecastList(weatherDateEpochSeconds)
                if (hourlyForecast == null) {
                    Timber.d("Hourly forecast list not found in local source")
                    return@withContext Result.Error( 3,
                        "Hourly forecast list not found in local source")
                }

                val dailyForecast = appDao.getDailyForecastList(weatherDateEpochSeconds)
                if (dailyForecast == null) {
                    Timber.d("Daily forecast list not found in local source")
                    return@withContext Result.Error( 4,
                        "Daily forecast list not found in local source")
                }

                oneCallWeather.hourlyForecast = hourlyForecast
                oneCallWeather.dailyForecast = dailyForecast
                return@withContext Result.Success(oneCallWeather)
            } catch (e: Exception) {
                Timber.d("Weather remote source returned error=$e")
                Result.Error(-1, e.toString())
            }
        }

    suspend fun deleteOldWeather(latestDateEpochSeconds: Int) =
        withContext(Dispatchers.IO) {
            appDao.deleteOldWeather(latestDateEpochSeconds)
        }

    suspend fun getFavWeatherList(
        sLocationList: List<ShortLocation>
    ) : Result<List<ShortWeather>> {
        val names = sLocationList.map { sLocation -> sLocation.name }
        return withContext(Dispatchers.IO) {
            return@withContext try {
                val sWeatherList: List<ShortWeather>? = appDao.getShortWeatherList(names)
                if (sWeatherList != null) {
                    Timber.d("Got sWeatherList=$sLocationList from local source")
                    Result.Success(sWeatherList)
                } else {
                    Timber.d("sWeatherList not found in local source")
                    Result.Error(5, "sWeatherList not found in local source")
                }
            } catch (e: Exception) {
                Timber.d("sWeatherList remote source returned error=$e")
                Result.Error(-1, e.toString())
            }
        }
    }


    suspend fun addFavLocation(sLocation: ShortLocation) =
        withContext(Dispatchers.IO) {
            appDao.insertFavLocation(sLocation)
        }

    suspend fun getFavLocationList(): Result<List<ShortLocation>> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val favLocationList = appDao.getFavLocationList()
                Timber.d("Got locationList=$favLocationList from local source")
                if (favLocationList == null) {
                    Timber.d("Location list null. Setting with empty list")
                    val emptyList: List<ShortLocation> = listOf()
                    Result.Success(emptyList)
                } else {
                    Result.Success(favLocationList)
                }
            } catch (e: Exception) {
                Timber.d("Location remote source returned error=$e")
                Result.Error(-1, e.toString())
            }
        }

    suspend fun deleteFavLocation(name: String) =
        withContext(Dispatchers.IO) {
            appDao.deleteShortLocation(name)
        }
}
