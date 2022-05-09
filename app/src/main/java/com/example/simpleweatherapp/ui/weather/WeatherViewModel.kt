package com.example.simpleweatherapp.ui.weather

import android.location.Location
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simpleweatherapp.data.MapsRepository
import com.example.simpleweatherapp.model.openweather.UnitSystem
import com.example.simpleweatherapp.data.WeatherRepository
import com.example.simpleweatherapp.model.bingmaps.ShortLocation
import com.example.simpleweatherapp.model.openweather.OneCallWeather
import com.example.simpleweatherapp.model.unbox
import com.example.simpleweatherapp.Const
import com.example.simpleweatherapp.util.oneEventBufferedSharedFlow
import com.example.simpleweatherapp.util.toUnitSystem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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

    private val _isWeatherAvailable: MutableSharedFlow<Boolean> =
        oneEventBufferedSharedFlow()
    val isWeatherAvailable: SharedFlow<Boolean> =
        _isWeatherAvailable.asSharedFlow()

    fun setWeather() {
        viewModelScope.launch {
            val sLocation = savedState.get<ShortLocation>(Const.LAST_LOCATION_KEY)
            if (sLocation == null) {
                _isWeatherAvailable.emit(false)
                return@launch
            }
            setWeather(sLocation)
        }
    }

    fun setWeather(location: Location) {
        viewModelScope.launch {
            val sLocation = getLocationByCoords(
                location.latitude,
                location.longitude
            )
            if (sLocation == null) {
                _isWeatherAvailable.emit(false)
                return@launch
            }
            savedState.set(Const.LAST_LOCATION_KEY, sLocation)
            setWeather(sLocation)
        }
    }

    fun setWeather(sLocation: ShortLocation) {
        viewModelScope.launch {
            val oneCallWeather = getOneCallWeather(
                sLocation.coords.latitude,
                sLocation.coords.longitude,
                Locale.getDefault().toUnitSystem()
            )
            if (oneCallWeather == null) {
                _isWeatherAvailable.emit(false)
                return@launch
            }
            _locationAndWeather.emit(Pair(sLocation, oneCallWeather))
            _isWeatherAvailable.emit(true)
        }
    }

    private suspend fun getOneCallWeather(
        latitude: Double,
        longitude: Double,
        unitSystem: UnitSystem
    ): OneCallWeather? =
        weatherRepository.getOneCallWeather(latitude, longitude, unitSystem).unbox()

    private suspend fun getLocationByCoords(
        latitude: Double,
        longitude: Double
    ): ShortLocation? = mapsRepository.getLocationByCoords(latitude, longitude).unbox()
}
//ShortLocation(
//"London, London, United Kingdom",
//"London",
//"England",
//"United Kingdom",
//Coords(51.500152587890625,
//-0.12623600661754608)
//)