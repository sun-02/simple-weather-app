package com.example.simpleweatherapp.ui.weather

import android.location.Location
import androidx.lifecycle.*
import com.example.simpleweatherapp.data.bingmaps.MapsRepository
import com.example.simpleweatherapp.data.openweather.WeatherRepository
import com.example.simpleweatherapp.model.bingmaps.ShortLocation
import com.example.simpleweatherapp.model.openweather.OneCallWeather
import com.example.simpleweatherapp.Const
import com.example.simpleweatherapp.util.oneEventBufferedSharedFlow
import com.example.simpleweatherapp.util.toUnitSystem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.example.simpleweatherapp.model.Result
import com.example.simpleweatherapp.model.openweather.ShortWeather
import com.example.simpleweatherapp.model.openweather.UnitSystem
import timber.log.Timber
import java.util.*

class WeatherViewModel(
    private val mapsRepository: MapsRepository,
    private val weatherRepository: WeatherRepository,
    val savedState: SavedStateHandle
) : ViewModel() {

    val favLocationList: LiveData<List<ShortLocation>> =
        mapsRepository.observeFavLocationList().map { result ->
        if (result is Result.Success) {
            result.data
        } else {
            listOf()
        }
    }

//    private val _favWeatherList: MutableSharedFlow<List<ShortWeather>> =
//        oneEventBufferedSharedFlow()
//    val favWeatherList: Flow<List<ShortWeather>> = favLocationList.map { locations ->
//
//    }

    private val localUnitSystem: UnitSystem = Locale.getDefault().toUnitSystem()

    private val _currentWeather: MutableSharedFlow<OneCallWeather> =
        oneEventBufferedSharedFlow()
    val currentWeather: SharedFlow<OneCallWeather> =_currentWeather.asSharedFlow()

    private val _isWeatherAvailable: MutableSharedFlow<Boolean> = oneEventBufferedSharedFlow()
    val isWeatherAvailable: SharedFlow<Boolean> = _isWeatherAvailable.asSharedFlow()


    init {
        val lastWeatherCleanup = savedState.get<Int>(Const.LAST_WEATHER_CLEANUP)
        val nowEpochMillis = (System.currentTimeMillis() / 1000).toInt()
        if (lastWeatherCleanup == null || nowEpochMillis - lastWeatherCleanup > Const.WEATHER_CLEANUP_INTERVAL) {
            viewModelScope.launch {
                Timber.d("Cleaning up weather older than" +
                        " ${Const.WEATHER_CLEANUP_INTERVAL / 60 / 60} hours")
                weatherRepository.deleteOldWeather(nowEpochMillis - Const.WEATHER_CLEANUP_INTERVAL)
            }
            savedState.set(Const.LAST_WEATHER_CLEANUP, nowEpochMillis)
        }

    }

    fun getFavWeather(): List<ShortWeather> {
        Timber.d("Getting weather for Favorites list")
        var favWeather: List<ShortWeather> = listOf()
        viewModelScope.launch {
            val locations = favLocationList.value!!
            for (loc in locations) {
                val weatherResult = weatherRepository.getWeather(loc, localUnitSystem)
                if (weatherResult is Result.Error) {
                    Timber.d("Weather repo returned error=$weatherResult")
                    _isWeatherAvailable.emit(false)
                }
            }
            Timber.d("Weather successfully prepared")
            _isWeatherAvailable.emit(true)
            val res = weatherRepository.getFavWeatherList(locations)
            if (res is Result.Success) {
                favWeather = res.data
            }
        }
        return favWeather
    }

//    fun refreshFavWeather() {
//        Timber.d("Setting favWeatherList")
//        viewModelScope.launch {
//            if (isWeatherAvailable.first()) {
//                val favWeatherResult = weatherRepository.getFavWeatherList(favLocationList.first())
//                if (favWeatherResult is Result.Success) {
//                    Timber.d("Got favWeatherList = $favWeatherResult")
//                    _favWeatherList.emit(favWeatherResult.data)
//                    _isWeatherAvailable.emit(true)
//                } else {
//                    Timber.d("Failed to get favWeatherList = $favWeatherResult")
//                    _isWeatherAvailable.emit(false)
//                }
//            }
//        }
//    }

    fun setWeather() {
        viewModelScope.launch {
            val sLocation = savedState.get<ShortLocation>(Const.LAST_LOCATION_KEY)
            if (sLocation == null) {
                _isWeatherAvailable.emit(false)
                Timber.d("Can't get location from SavedStateHandle")
                return@launch
            }
            Timber.d("Got location from SavedStateHandle ${sLocation.name}")
            setWeather(sLocation)
        }
    }

    fun setWeather(location: Location) {
        viewModelScope.launch {
            val mapsResult = mapsRepository
                .getRemoteLocation(location.latitude, location.longitude)
            when (mapsResult) {
                is Result.Error -> {
                    _isWeatherAvailable.emit(false)
                    Timber.d("Maps repo returned error=$mapsResult")
                }
                is Result.Success -> {
                    val sLocation: ShortLocation = mapsResult.data[0]
                    Timber.d("Got sLocation by coords from repo ${sLocation.name}")
                    savedState.set(Const.LAST_LOCATION_KEY, sLocation)
                    Timber.d("Saved last sLocation=\"${sLocation.name}\" in SavedStateHandle")
                    setWeather(sLocation)
                }
            }
        }
    }

    fun setWeather(sLocation: ShortLocation) {
        viewModelScope.launch {
            val weatherResult = weatherRepository.getWeather(
                sLocation,
                localUnitSystem
            )
            when (weatherResult) {
                is Result.Error -> {
                    _isWeatherAvailable.emit(false)
                    Timber.d("Weather repo returned error=$weatherResult")
                }
                is Result.Success -> {
                    val oneCallWeather: OneCallWeather = weatherResult.data
                    Timber.d("Got weather=$oneCallWeather from repo")
                    _isWeatherAvailable.emit(true)
                    _currentWeather.emit(oneCallWeather)
                    Timber.d("Weather=$oneCallWeather has been set")
                }
            }
        }
    }

    fun addFavLocation(sLocation: ShortLocation) {
        viewModelScope.launch {
            mapsRepository.addFavLocation(sLocation)
        }
    }

    fun removeFavLocation(name: String) {
        viewModelScope.launch {
            mapsRepository.removeFavLocation(name)
        }
    }
}
