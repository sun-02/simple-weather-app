package com.example.simpleweatherapp.ui.weather

import android.location.Location
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import timber.log.Timber
import java.util.*

class WeatherViewModel(
    private val mapsRepository: MapsRepository,
    private val weatherRepository: WeatherRepository,
    private val savedState: SavedStateHandle
) : ViewModel() {

    private val _locationAndWeather: MutableSharedFlow<Pair<ShortLocation, OneCallWeather>> =
        oneEventBufferedSharedFlow()
    val locationAndWeather: SharedFlow<Pair<ShortLocation, OneCallWeather>> =
        _locationAndWeather.asSharedFlow()

    private val _isWeatherAvailable: MutableSharedFlow<Boolean> = oneEventBufferedSharedFlow()
    val isWeatherAvailable: SharedFlow<Boolean> = _isWeatherAvailable.asSharedFlow()

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
                .getLocation(location.latitude, location.longitude)
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
            val weatherResult = weatherRepository.getOneCallWeather(
                sLocation,
                Locale.getDefault().toUnitSystem()
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
                    _locationAndWeather.emit(Pair(sLocation, oneCallWeather))
                    Timber.d("Weather=$oneCallWeather has been set")
                }
            }
        }
    }

//    private suspend fun getOneCallWeather(
//        latitude: Double,
//        longitude: Double,
//        unitSystem: UnitSystem
//    ): Result<OneCallWeather> =
//        weatherRepository.getOneCallWeather(latitude, longitude, unitSystem).unbox()
//
//    private suspend fun getLocationByCoords(
//        latitude: Double,
//        longitude: Double
//    ): ShortLocation? = mapsRepository.getLocationByCoords(latitude, longitude).unbox()?.get(0)
}
//ShortLocation(
//"London, London, United Kingdom",
//"London",
//"England",
//"United Kingdom",
//Coords(51.500152587890625,
//-0.12623600661754608)
//)