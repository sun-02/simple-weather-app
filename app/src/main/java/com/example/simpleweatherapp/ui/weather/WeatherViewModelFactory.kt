package com.example.simpleweatherapp.ui.weather

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.example.simpleweatherapp.data.bingmaps.MapsRepository
import com.example.simpleweatherapp.data.openweather.WeatherRepository

@Suppress("UNCHECKED_CAST")
class WeatherViewModelFactory(
    private val mapsRepository: MapsRepository,
    private val weatherRepository: WeatherRepository,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ) = with(modelClass) {
        when {
            isAssignableFrom(WeatherViewModel::class.java) ->
                WeatherViewModel(
                    mapsRepository,
                    weatherRepository,
                    handle
                )
            else ->
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    } as T
}