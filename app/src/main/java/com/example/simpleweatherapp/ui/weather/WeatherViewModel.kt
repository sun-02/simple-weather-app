package com.example.simpleweatherapp.ui.weather

import android.location.Location
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.example.simpleweatherapp.Const
import com.example.simpleweatherapp.data.bingmaps.MapsRepository
import com.example.simpleweatherapp.data.openweather.WeatherRepository
import com.example.simpleweatherapp.model.Result
import com.example.simpleweatherapp.model.bingmaps.ShortLocation
import com.example.simpleweatherapp.model.openweather.OneCallWeather
import com.example.simpleweatherapp.model.openweather.ShortWeather
import com.example.simpleweatherapp.model.openweather.UnitSystem
import com.example.simpleweatherapp.util.oneEventBufferedSharedFlow
import com.example.simpleweatherapp.util.toUnitSystem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class WeatherViewModel(
    private val mapsRepository: MapsRepository,
    private val weatherRepository: WeatherRepository,
    val savedState: SavedStateHandle
) : ViewModel() {

    private val localUnitSystem: UnitSystem = Locale.getDefault().toUnitSystem()

    private val _selectedWeather: MutableSharedFlow<OneCallWeather> =
        oneEventBufferedSharedFlow()
    val selectedWeather: SharedFlow<OneCallWeather> = _selectedWeather.asSharedFlow()

    private val _currentLocationWeather: MutableSharedFlow<OneCallWeather> =
        oneEventBufferedSharedFlow()
    val currentLocationWeather: SharedFlow<OneCallWeather> = _currentLocationWeather.asSharedFlow()

    val favLocationList: LiveData<List<ShortLocation>> =
        mapsRepository.observeFavLocationList()

    private val _favWeatherList: MutableSharedFlow<List<ShortWeather>> =
        oneEventBufferedSharedFlow()
    val favWeatherList: SharedFlow<List<ShortWeather>> = _favWeatherList.asSharedFlow()

    private val _isWeatherAvailable: MutableSharedFlow<Boolean> = oneEventBufferedSharedFlow()
    val isWeatherAvailable: SharedFlow<Boolean> = _isWeatherAvailable.asSharedFlow()

    private var _favWeatherObserver: Observer<List<ShortLocation>>? = Observer { locations ->
        viewModelScope.launch {
            for (loc in locations) {
                when (val isWeatherSavedResult = weatherRepository.isWeatherSaved(loc.name)) {
                    is Result.Error -> {
                        Timber.d("Weather repo returned error=$isWeatherSavedResult")
                        _isWeatherAvailable.emit(false)
                        return@launch
                    }
                    is Result.Success -> {
                        if (!isWeatherSavedResult.data) {
                            val weatherResult = weatherRepository.getWeather(loc, localUnitSystem)
                            if (weatherResult is Result.Error) {
                                Timber.d("Weather repo returned error=$weatherResult")
                                _isWeatherAvailable.emit(false)
                                return@launch
                            }
                        }
                    }
                }
            }
            Timber.d("Weather successfully prepared")
            _isWeatherAvailable.emit(true)
            val favWeatherListResult = weatherRepository.getFavWeatherList(locations)
            if (favWeatherListResult is Result.Success) {
                val sortedWeatherList = List<ShortWeather>(locations.size) {
                    val lName = locations[it].name
                    favWeatherListResult.data.first { sw -> sw.name == lName }
                }
                _favWeatherList.emit(sortedWeatherList)
            } else {
                Timber.d("Weather repo returned error=$favWeatherListResult")
                _isWeatherAvailable.emit(false)
                return@launch
            }
        }
    }
    private val favWeatherObserver get() = _favWeatherObserver!!

    init {
        cleanOldWeather()
    }

    private fun cleanOldWeather() {
        val lastWeatherCleanup = savedState.get<Long>(Const.LAST_WEATHER_CLEANUP)
        val nowEpochMillis = System.currentTimeMillis()
        if (lastWeatherCleanup == null ||
            nowEpochMillis - lastWeatherCleanup > Const.WEATHER_CLEANUP_INTERVAL) {
            viewModelScope.launch {
                Timber.d(
                    "Cleaning up weather older than" +
                            " ${Const.WEATHER_CLEANUP_INTERVAL / 60 / 60} hours"
                )
                weatherRepository.deleteOldWeather(
                    nowEpochMillis - Const.WEATHER_CLEANUP_INTERVAL)
            }
            savedState.set<Long>(Const.LAST_WEATHER_CLEANUP, nowEpochMillis)
        }
    }

    fun refreshFavWeather() {
        favLocationList.observeForever(favWeatherObserver)
    }

    fun refreshWeather() {
        viewModelScope.launch {
            val sLocation = savedState.get<ShortLocation>(Const.LAST_LOCATION_KEY)
            if (sLocation == null) {
                _isWeatherAvailable.emit(false)
                Timber.d("Can't get location from SavedStateHandle")
                return@launch
            }
            Timber.d("Got location from SavedStateHandle ${sLocation.name}")
            refreshWeather(sLocation)
        }
    }

    fun refreshWeather(location: Location) {
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
                    savedState.set<ShortLocation>(Const.CURRENT_LOCATION_KEY, sLocation)
                    refreshWeather(sLocation, true)
                }
            }
        }
    }

    fun refreshWeather(sLocation: ShortLocation, isCurrentLocation: Boolean = false) {
        savedState.set(Const.LAST_LOCATION_KEY, sLocation)
        Timber.d("sLocation=\"${sLocation.name}\" saved to SavedStateHandle")
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
                    _selectedWeather.emit(oneCallWeather)
                    if (isCurrentLocation) _currentLocationWeather.emit(oneCallWeather)
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

    fun removeFavLocation(sLocation: ShortLocation) {
        viewModelScope.launch {
            mapsRepository.removeFavLocation(sLocation)
        }
    }

    override fun onCleared() {
        _favWeatherObserver = null
        super.onCleared()
    }
}
