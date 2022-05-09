package com.example.simpleweatherapp.ui.weather

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.example.simpleweatherapp.data.MapsRepository
import com.example.simpleweatherapp.data.WeatherRepository
import com.example.simpleweatherapp.ui.search.SearchViewModel
import com.google.android.gms.location.FusedLocationProviderClient

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
            isAssignableFrom(SearchViewModel::class.java) ->
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