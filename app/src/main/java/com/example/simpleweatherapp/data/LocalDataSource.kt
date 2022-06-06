package com.example.simpleweatherapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
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
                val parentName = oneCallWeather.name
                val hourlyForecast = appDao.getHourlyForecastList(parentName)
                if (hourlyForecast == null) {
                    Timber.d("Hourly forecast list not found in local source")
                    return@withContext Result.Error( 3,
                        "Hourly forecast list not found in local source")
                }

                val dailyForecast = appDao.getDailyForecastList(parentName)
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
                val sWeatherList: List<ShortWeather>? = appDao.getFavWeatherList(names)
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

    fun observeFavLocationList(): LiveData<Result<List<ShortLocation>>> {
        return appDao.observeFavLocationList().map { favLocationList ->
            Timber.d("Got locationList=$favLocationList from local source")
            if (favLocationList == null) {
                Timber.d("Location list null. Setting with empty list")
                val emptyList: List<ShortLocation> = listOf()
                Result.Success(emptyList)
            } else {
                Result.Success(favLocationList)
            }
        }
    }

    suspend fun deleteFavLocation(name: String) =
        withContext(Dispatchers.IO) {
            appDao.deleteShortLocation(name)
        }
}
